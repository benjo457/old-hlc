#!/usr/bin/env python

# lists : squares = [1, 4, 9, 16, 25]
# tuple : t = 12345, 54321, 'hello!'
# set   : basket = {'apple', 'orange', 'apple', 'pear', 'orange', 'banana'}
# dict  : tel = {'jack': 4098, 'sape': 4139}
#
#  dn:
#  a1: v1
#
#  dn: o=o1
#  o: o1
#
#  dn: c=c1, o=o1
#  c: c1
#

import sys
import sqlite3
import collections
import pickle
import pprint
import ldap3.utils.dn

hlc_db = "hlc.db"
hlc_schema = """
    CREATE TABLE IF NOT EXISTS entry (
        id INTEGER PRIMARY KEY,
        parent_id INTEGER,
        rdn_id INTEGER,
        FOREIGN KEY(parent_id) REFERENCES entry(id),
        FOREIGN KEY(rdn_id) REFERENCES attr(id)
    );
    CREATE TABLE IF NOT EXISTS attr (
        id INTEGER PRIMARY KEY,
        entry_id INTEGER,
        name TEXT,
        value,
        FOREIGN KEY(entry_id) REFERENCES entry(id),
        UNIQUE(entry_id, name, value)
    );
             """
hlc_root = {
               'type': 'hlc_root',
               'version': 0.1
           }
            
class HlcMissingParent(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)
            
class HlcMissingRdnAttr(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)
        
class HlcDupRdn(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)

class HlcBadRdn(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)

def init():
    try:
        with sqlite3.connect(hlc_db) as conn:
            cu = conn.cursor()
            cu.executescript(hlc_schema)
            # create root if db empty
            cu.execute("SELECT COUNT(*) from entry")
            res=cu.fetchone()
            if res[0] == 0:
                print("Adding db root")
                cu.execute("INSERT INTO entry VALUES ('1', null, null)")
                cu.executemany("INSERT INTO attr VALUES (null, ?, ?, ?)",
                               [(1, key, val) for (key, val) in hlc_attribs(hlc_root)])
            else:
                pass
            # dump db
    except sqlite3.Error as err:
        print("SQLITE error: {0}".format(err))

def hlc_attribs(attrs_dict):
    for attr_key in attrs_dict:
        attr_val = attrs_dict[attr_key]
        for val in hlc_encode(attr_val):
            yield (attr_key, val)
            
def hlc_encode(val):
    if type(val) in (int, str, float):
        yield val
    elif type(val) is set:
        for elem in val:
            for subval in hlc_encode(elem):
                yield subval
    else:
        yield pickle.dumps(val)
        
def hlc_decode(val):
    if type(val) in (int, str, float):
        return val
    else:
        return pickle.loads(val)

def hlc_add_entry(str_dn, attrs):
    dn = ldap3.utils.dn.parse_dn(str_dn)
    parent_id = 1
    print ("Adding entry:",str_dn)
    try:
        # find parent id
        for rdn in reversed(dn[1:]):
            attr_name=rdn[0]
            attr_val=rdn[1]
            matched_dn = ''
            #print("attr_name:", attr_name)
            #print("attr_val:", attr_val)
            with sqlite3.connect(hlc_db) as conn:
                cu = conn.cursor()
                cu.execute("""
                           SELECT e.id FROM entry e, attr a
                           WHERE e.parent_id = ? AND
                           e.rdn_id = a.id AND
  	                       a.name = ? AND
	                       a.value = ?
	                       """ , ( parent_id, attr_name, attr_val ))
                result = cu.fetchone()
                if result != None:
                    parent_id = result[0]
                    matched_dn = ',' + attr_name + '=' + attr_val + matched_dn
                else:
                    raise HlcMissingParent(attr_name + '=' + attr_val + matched_dn)
        # check no dup rdn
        with sqlite3.connect(hlc_db) as conn:
            cu = conn.cursor()
            cu.execute("""
                       SELECT a.id FROM entry e, attr a
                       WHERE e.parent_id = ? AND
                       a.entry_id = e.id AND
  	                   a.name = ? AND
	                   a.value = ?
	                   """ , ( parent_id, dn[0][0], dn[0][1] ))
            result = cu.fetchone()
            if result != None:
                raise HlcDupRdn(str_dn)
	    # add entry
        with sqlite3.connect(hlc_db) as conn:
            cu = conn.cursor()
            cu.execute("INSERT INTO entry VALUES (null, ?, null)", (parent_id,))
            entry_id = cu.lastrowid
            rdn_id = None
            for attr_row in [(entry_id, key, val) for (key, val) in hlc_attribs(attrs)]:
                cu.execute("INSERT INTO attr VALUES (null, ?, ?, ?)", attr_row)
                if attr_row[1] == dn[0][0] and attr_row[2] == dn[0][1] :
                    rdn_id = cu.lastrowid
            if rdn_id:
                cu.execute("UPDATE entry SET rdn_id=? WHERE id=?" , (rdn_id , entry_id))
            else:
                raise HlcMissingRdnAttr(dn[0][0] + ': ' + dn[0][1])
        print ("Added id:",entry_id)
        return entry_id
    except sqlite3.Error as err:
        print("SQLITE error: {0}".format(err))
    
def hlc_dump(entry_id=1):
    print("dn:",hlc_get_dn(entry_id))
    pprint.pprint(hlc_get_attribs(entry_id))
    print()
    with sqlite3.connect(hlc_db) as conn:
        cu = conn.cursor()
        cu.execute("""
                   SELECT id FROM entry
                   WHERE parent_id = ?
                   """ , ( entry_id, ))
        result = cu.fetchall()
        for son_id in result:
            hlc_dump(son_id[0])

def hlc_get_dn(entry_id):
    if entry_id == 1:
        return None
    with sqlite3.connect(hlc_db) as conn:
        cu = conn.cursor()
        cu.execute("""
                   SELECT e.parent_id, a.name, a.value FROM entry e, attr a
                   WHERE e.id = ? AND
                   e.rdn_id = a.id
                   """ , ( entry_id, ))
        result = cu.fetchone()
        if result != None:
            parent_dn =  hlc_get_dn(result[0])
            if parent_dn:
                return result[1] + '=' + result[2] + ', ' + parent_dn
            else:
                return result[1] + '=' + result[2]
        else:
            raise HlcBadRdn(entry_id)

def hlc_get_attribs(entry_id):
    with sqlite3.connect(hlc_db) as conn:
        cu = conn.cursor()
        cu.execute("""
                   SELECT name, value FROM attr
                   WHERE entry_id = ?
                   """ , ( entry_id, ))
        result = cu.fetchall()
        attribs = {}
        for attr in result:
            if attr[0] in attribs:
                if type(attribs[attr[0]]) is set:
                    attribs[attr[0]].add(hlc_decode(attr[1]))
                else:
                    v = attribs[attr[0]]
                    s = set()
                    s.add(v)
                    s.add(hlc_decode(attr[1]))
                    attribs[attr[0]] = s
            else:
                attribs[attr[0]] = hlc_decode(attr[1])            
        return attribs

init()
hlc_add_entry('o=olib',
              {'o': 'olib', 'type' : 'orga'})
hlc_add_entry('ou=people,o=olib',
              {'ou': 'people', 'type' : 'unit'})
hlc_add_entry('sn=testadd_sn,ou=people,o=olib',
              {'cn': 'testadd_cn', 'sn': 'testadd_sn', 'type' : 'person'})
hlc_add_entry('ou=groups,o=olib',
              {'ou': 'groups', 'type' : 'unit'})
x_id = hlc_add_entry('name=lmg,ou=people,o=olib',
              {'name': 'lmg',
               'age': 50,
               'squares': [1, 4, 9, 16, 25],
               'basket': {'apple', 'orange', (1, 4, 9, 16, 25), 'apple', 'pear', 'orange', 'banana'} })
x_dn = hlc_get_dn(x_id)
x_attribs = hlc_get_attribs(x_id)
#print("dn:",x_dn)
#pprint.pprint(x_attribs)
hlc_dump()

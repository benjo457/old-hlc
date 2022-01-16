#!/usr/bin/env python

import platform
import sys
import wx
import wx.dataview as dv
from wx.__version__ import VERSION, VERSION_STRING

hlc_version = "v0.0.1"

class HLCFrame(wx.Frame):
    def __init__(self, *args, **kw):
        super(HLCFrame, self).__init__(*args, **kw)
        self.splitter = wx.SplitterWindow(self, -1)

        leftPanel = wx.Panel(self.splitter, -1)
        leftBox = wx.BoxSizer(wx.VERTICAL)
        leftPanel.SetSizer(leftBox)

        self.tree = dv.DataViewTreeCtrl(leftPanel)
#        root1 = self.tree.AppendContainer(dv.NullDataViewItem, "Root1")
#        root2 = self.tree.AppendContainer(dv.NullDataViewItem, "Root2")
#        self.tree.AppendItem(root1, "child1")
#        self.tree.AppendItem(root2, "child2")
        leftBox.Add(self.tree, 1, wx.EXPAND)

        rightPanel = wx.Panel(self.splitter, -1)
        rightBox = wx.BoxSizer(wx.VERTICAL)
        rightPanel.SetSizer(rightBox)

        self.list = dv.DataViewListCtrl(rightPanel, wx.ID_ANY)
#        self.list.AppendToggleColumn("Toggle")
#        self.list.AppendTextColumn("Text")
#        data = [True, "row 1"]
#        self.list.AppendItem(data)
#        data = [False, "row 3"]
#        self.list.AppendItem(data)
        rightBox.Add(self.list, 1, wx.EXPAND)

        self.splitter.SplitVertically(leftPanel, rightPanel)
        self.splitter.SetSashPosition(250)

        self.makeMenuBar()
        
        self.CreateStatusBar()
        self.SetStatusText("HLC v {}".format(hlc_version))
        self.Centre()
        self.SetSize(wx.Size(1000, 500))

    def makeMenuBar(self):
        """
        A menu bar is composed of menus, which are composed of menu items.
        This method builds a set of menus and binds handlers to be called
        when the menu item is selected.
        """

        # Make a file menu with Hello and Exit items
        fileMenu = wx.Menu()
        # The "\t..." syntax defines an accelerator key that also triggers
        # the same event
        helloItem = fileMenu.Append(-1, "&Hello...\tCtrl-H",
                "Help string shown in status bar for this menu item")
        fileMenu.AppendSeparator()
        # When using a stock ID we don't need to specify the menu item's
        # label
        exitItem = fileMenu.Append(wx.ID_EXIT)

        # Now a help menu for the about item
        helpMenu = wx.Menu()
        aboutItem = helpMenu.Append(wx.ID_ABOUT)

        # Make the menu bar and add the two menus to it. The '&' defines
        # that the next letter is the "mnemonic" for the menu item. On the
        # platforms that support it those letters are underlined and can be
        # triggered from the keyboard.
        menuBar = wx.MenuBar()
        menuBar.Append(fileMenu, "&File")
        menuBar.Append(helpMenu, "&Help")

        # Give the menu bar to the frame
        self.SetMenuBar(menuBar)

        # Finally, associate a handler function with the EVT_MENU event for
        # each of the menu items. That means that when that menu item is
        # activated then the associated handler function will be called.
        self.Bind(wx.EVT_MENU, self.OnHello, helloItem)
        self.Bind(wx.EVT_MENU, self.OnExit,  exitItem)
        self.Bind(wx.EVT_MENU, self.OnAbout, aboutItem)


    def OnExit(self, event):
        """Close the frame, terminating the application."""
        self.Close(True)


    def OnHello(self, event):
        """Say hello to the user."""
        wx.MessageBox("Hello again from wxPython")


    def OnAbout(self, event):
        """Display an About Dialog"""
        wx.MessageBox("This is a wxPython Hello World sample",
                      "About Hello World 2",
                      wx.OK|wx.ICON_INFORMATION)

        
if __name__ == "__main__":
    hlc = wx.App()
    frm = HLCFrame(None, title="hlc")
    frm.Show()
    hlc.MainLoop()
    

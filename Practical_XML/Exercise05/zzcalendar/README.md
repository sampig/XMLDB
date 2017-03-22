Practical XML: Calendar
==========================

Author: [ZHU, Chenfeng](http://about.me/zhuchenfeng)

## Table of contents

* [Features](#features)
* [Manual](#manual)

## Features

1. Show calendar.
    1. Calendar view.
    2. Appointments list.
2. Show entries.
    1. All requested entries locally.
    2. All requested entries locally and remotely.
3. Show free slots.
    1. All available free slots and the first one according to local source.
    2. All available free slots and the first one according to both local source and remote source.
4. Configuration.
    1. Change XML source.

## Manual

1. Modify _resources/config.properties_:
    1. Change the location of XSD/XML and URL of web service initially.
    2. Location of XML and URL of web service can be changed in the web interface.
2. Build the war package.
3. Copy the package to server (e.g. tomcat).
4. Run it on server.


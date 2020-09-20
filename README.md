# AndroidSimpleLogToFile

Created by Henry Tan, Sep 20, 2020

----

## Introduction

* Easy to integrate into any AS project for debugging. Allow you to log everything you want to the file with specified path.
* Reducing I/O frequency with local cache. To improve the performace, it won't write to file everytime when you call the method. It will batch the logs and write to file.
* Customizing your config for the delay time and the cache maximum size. All the logs created will only be written in the file when reach the maximun amount (20 by default) or exceed the delay time (20 seconds by default). You can change these two criterias
in the code.

What you can find and see in the log file looks like the following content : 

```
======== Log to File (2020-09-20 14:50:18) ========
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record

======== Log to File (2020-09-20 14:50:39) ========
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
Add a new log record
```

## How to Integrate

### Integrate it by copying file.
Everything is implemented in one file `LogToFile.java`, so you don't need to config maven or static jar in `Build.gradle` file. What you need to do is just copying this file into your project. Suggest to place it in `../log/LogToFile.java`, which can keep your existing repo clean.

### Permission
* Remember to decline write external storage permission in your `AndroidManifest.xml` file.

```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

* Toggle on the permission in system setting manually or ask for permission granted in runtime.


### What's to customized
* `static final int WRITING_DELAY` : the file writing delay time
* `static final int MAX_CACHE_SIZE` : the log item cache size
* `static String mRootDir` : the external storage root directory
* `static String mDirName` : the dir for this log project.
* `static String mFileName` : the log file name.
 
### What's the way to call

* Update log file name before use.

```
LogToFile.setFileName("simple_log_to_file_demo.log");

```

* Call the method to log

```
LogToFile.logToFile(content);
```

For easier to filter the content in log file, you better to define your own format for the content String, such as start with some tags : 

```
String content = "<TimeStamp> #Tag1# #Tag2# <log content string>"
```
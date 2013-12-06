Filename: readme.txt
Author: Peter Piech
Date: 12/4/2013

-------------------------------------------------
-------------------------------------------------

When building the application for release, due to
the use of the Google Maps Android v2 API, please
follow these instructions for DEBUG and RELEASE
versions of the application.

-------------------------------------------------
-------------------------------------------------

DEBUG:
 - You are testing the app in the emulator or on
     your personal phone

1) Make sure the AndroidManifest.xml is
   indicating the correct Google Maps v2
   Android API key for debugging. Make sure the
   release API key is commented out.

2) Either copy the file "debug.keystore" to
   "c:\Users\<username>\.android" and
   build as you normally would in Eclipse.

    /================================\
   < ---------------OR:-------------- >
    \================================/

    Manually build the application in
    Eclipse by clicking:
    File->Export->Export Android Application
    then click Next.

    Choose the RPIMobile project and click next.

    Choose "Use existing keystore".
    Browse to the location of "debug.keystore".
    Enter "android" (without quotes) into the
    password field and click next.

    Choose "Use existing key".
    Select "androiddebugkey" for Alias.
    Enter "android" into the password field.
    Click next.

    Choose a destination for the binary APK.
    Click finish.

    Open a command prompt in the directory you
    saved the APK in and use adb to install it
    to your personal phone.

------------------------------------------------
------------------------------------------------
RELEASE:
 - You are going to release the application to
     the public on the Google Play Store or
        elsewhere.

1) Make sure the AndroidManifest.xml is
   indicating the correct Google Maps v2
   Android API key for release. Make sure the
   debug API key is commented out.

2)  manually build the application in
    Eclipse by clicking:
    File->Export->Export Android Application
    then click Next.

    Choose the RPIMobile project and click next.

    Choose "Use existing keystore".
    Browse to the location of "release.keystore".
    Enter <not_this> into the
    password field and click next.

    Choose "Use existing key".
    Select "rpimobile1" for Alias.
    Enter <not_this> into the password field.
    Click next.

    Choose a destination for the binary APK.
    Click finish.  

3) release the application  
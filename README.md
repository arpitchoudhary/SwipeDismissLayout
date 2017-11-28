SwipeDismissLayout
===================

SwipeDismissLayout is a viewgroup which if added as a parent viewgroup in the layout (activity) container, can enables the capability to finish the activity/fragment via slide down gesture.

----------

Requirements
-------------
Have tested on Android **API Level 14+**

----------

Demo
-------------------
Watch a short demo on youtube. Here is the [link](https://youtu.be/53ichWG7D_E)
Currently I am using this viewgroup with the webview activity. 
But can be used with any other activity. In case of fragment, the holder activity layout should have the viewgroup added as a parent. you can also check [here](https://github.com/arpitchoudhary/SwipeDismissLayout/blob/master/app/src/main/res/layout/webview.xml) how i have used this view group in layout file.


Usage
-------------
Add a dependency to your build.gradle

>  dependencies  {
	     compile 'com.viewgroup:swipedismisslayout:1.0.0'
}

----------
Version
-------------
1.0.0

 ----------

License
-------------
 Copyright 2017 Arpit Choudhary

>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  > http://www.apache.org/licenses/LICENSE-2.0

>Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

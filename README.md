TeamCity Deployment Dashboard
=============================

A TeamCity plugin that shows a high level view of what builds have been deployed into what environments across multiple projects.

Features
--------

* Single pane of glass showing all deployments into different environments for the current project and all subprojects.
* Visualises deployment result, showing success, error, or in progress.
* Shows deployment time, or date if not today.
* Links to deployment build.
* Lists build version as taken from the build number.
* Highlights latest versions to emphasise build journey to production (derived by assuming semver formatted version string).
* Provides real-time search to quickly narrow large numbers of projects.
* Pretty.


Example
-------

<img src="screenshot.png" width="734"/>


Getting Started
---------------

1. Install the plugin via the TeamCity Plugin Portal.
2. Restart TeamCity as prompted.
3. Enable plugin in project configuration, listed as a new entry in the sidebar "Deployment Dashboard".

Once enabled, a new "Deployments" project tab will appear on deployment builds showing the dashboard content, and all subprojects.

A deployment build in TeamCity can be selected from the build configuration's "General" tab. This changes a build's "Run" button to "Deploy" and lists it on the build overview.

Configuration
-------------

Enabling the plugin in the configuration will show three settings.

1. Project key: This is used to customise the project name shown in the dashboard and can be used in the case where the name of the immediate project is not appropriate. If it is appropriate, leave this blank, otherwise the dashboard will be picked up from a build property named by this key.
2. Environment key: This signifies the build property that indicates the environment name, e.g. Dev, UAT.
3. Environments: This lists all the possible environments, and will determine the columns shown on the dashboard. If the environment of the build is not in this list, it will not appear on the dashboard.

Projects inherit parent configuration unless overridden at a lower level. If all projects in a TeamCity instance are the same, the configuration only needs to be set in the root project.


Deployment Visibility
---------------------

Builds in TeamCity will be performed for many reasons and not all of them will be relevant to show on the dashboard. This plugin will only show builds if the following are true:

1. Has a build configuration type set to `Deployment` and therefore has a 'Deploy' button rather than 'Run'
 * See: TeamCity / Project Configuration / Build configuration type
2. Has a configuration parameter matching the project key when not blank.
3. Has a configuration parameters that matches the environment key.
4. The environed is contained in the list of configured environments.


Implementation
--------------

This project is written in JavaScript using React for the frontend and Kotlin for the backend holding the configuration and querying the build data.


Possible Future Features
------------------------

* Finish making project and environment query work from configured values.
* Finish only show deployment tab if configuration is enabled at this or parent.
* Support different project and env values when showing the dashboard at a higher level.
* Show real-time progress by having the frontend subscribe to build changes via server-sent events.

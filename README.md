TeamCity Deployment Dashboard
=============================

A TeamCity plugin that shows a high level view of what builds have been deployed into what
environments across multiple projects.

Features
--------

* Single pane of glass showing all deployments into different environments.
* Shows deployments for the current project and all sub-projects, and can be configured at any
level to show different environments.
* Visualises deployment result, showing success, error, or in progress.
* Shows deployment time, or date if not today.
* Dashboard deployments link to the original deployment build.
* Shows deployment version as taken from the build number.
* Highlights the latest version to emphasise build journey to production (assuming semver-formatted
version string).
* Provides real-time search to quickly narrow large numbers of projects.
* Pretty.


Example
-------

<img src="screenshot.png" width="954"/>


Getting Started
---------------

1. Install the plugin via the TeamCity Plugin Portal.
2. Restart TeamCity as prompted.
3. Enable plugin in project configuration, listed as a new entry in the sidebar "Deployment Dashboard".

Once enabled, a new "Deployments" project tab will appear on deployment builds showing the dashboard
content, and all subprojects.

A deployment build in TeamCity can be selected from the build configuration's "General" tab. This
changes a build's "Run" button to "Deploy" and lists it on the build overview.




Configuration
-------------

Enabling the plugin in the configuration will show three settings.

<img src="screenshot-config.png" width="918"/>

1. Project key: This is used to customise the project name shown in the dashboard and can be used
in the case where the name of the immediate project is not appropriate. If it is appropriate, leave 
this blank, otherwise the dashboard will be picked up from the build property named by this key.
2. Environment key: This signifies the build property that indicates the environment name, e.g. Dev, UAT.
3. Environments: This lists all the possible environments, and will determine the columns shown on
the dashboard. If the environment of the build is not in this list, it will not appear on the dashboard.

Projects inherit parent configuration unless overridden at a lower level. If all projects in a 
TeamCity instance are the same, the configuration only needs to be set in the root project.


Environments
------------

TeamCity does not have (or need), the notion of a deployment environment. However, the dashboard 
needs to be able to correlate build configurations (and the deployment builds that are created) 
with an environment so it can be placed in the relevant part of the dashboard.

The most natural way to do this for TeamCity is to split deployments into environments by using 
separate build configuration for each one so they can be represented on a pipeline (or build chain 
in TeamCity parlance).

The plugin can then classify the build deployments performed by these configurations with each 
environment, either by a configuration parameter or by the name of the build configuration. Note 
it's probably better to use a parameter to indicate the environment, as the deployment name is 
more likely an action such as "Deploy Dev".


Scaling to Multiple Teams with the Project Hierarchy
----------------------------------------------------

The dashboard shows deployments for the current project and all sub-projects. This allows the 
greatest visibility at the higher levels. However, it can be hard to find consistency between 
multiple teams, what their environments are called and what properties in TeamCity they use to 
classify them.

There are two ways to handle this:

1. Only enable the dashboard at lower levels of the project hierarchy. Ideally teams would have 
their own configuration branch in TeamCity where it is much easier to make deployment builds 
consistent.
2. Only define the environments that all teams can agree on at the top levels of the project 
hierarchy, such as Development, UAT and Production. Then override the dashboard configuration 
at lower levels to be more specific. A team that has an additional performance testing 
environment can then see all their deployments, the additional environment just won't be shown 
at the top level.


Deployment Visibility
---------------------

Builds in TeamCity will be performed for many reasons and not all of them will be relevant to 
show on the dashboard. This plugin will only show builds if the following are true:

1. Has a build configuration type set to `Deployment` and therefore has a 'Deploy' button rather 
than 'Run'
   * See: TeamCity / Project Configuration / Build configuration type
2. Has a configuration parameter matching the project key when not blank.
3. Has a configuration parameters that matches the environment key when not blank.
4. The environment deployed to is contained in the list of configured environments.


Implementation
--------------

This project is written in JavaScript using React for the frontend and Kotlin for the backend 
holding the configuration and querying the build data.


Possible Future Features
------------------------

* Allow using environment/system variables as well as configuration properties to interpret
  project/environment from builds.
* Show real-time progress by having the frontend subscribe to build changes via server-sent events.

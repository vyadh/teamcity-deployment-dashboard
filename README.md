TeamCity Deployment Dashboard
=============================

A web app to query the TeamCity REST API for deployment builds and show what has been deployed in what environment.


Example
-------

<img src="screenshot.png" width="734"/>


Getting Started
---------------

Getting just the frontend talking to the TeamCity REST API:

1. Download and install latest release into any web server and note the deployment location
2. Update `configuration.js` with the TeamCity base URL
3. Update `configuration.js` with the environment names in the order they should be shown
4. Configure TeamCity to accept CORS requests:
  * Go to Administration / Diagnostics / Internal Properties
  * Permit access from dashboard: `rest.cors.origins=http://<deployment host/port>`
6. Ensure you are logged in to TeamCity
7. Visit deployment location in the same browser, your credentials will be used to make the REST requests

Using the TeamCity plugin version, just download and install.


Deployment Visibility
---------------------

Builds in TeamCity will be performed for many reasons and not all of them will be relevant to show on the dashboard.
In this app, builds will only be shown if the following are true:

1. Has a configuration type set to `Deployment` and therefore has a 'Deploy' button rather than 'Run'
 * See: TeamCity / Project Configuration / Build configuration type
2. Has a configuration parameter of `PROJECT` of the name to show
3. Has a configuration parameter of `ENVIRONMENT` that matches an environment within `configuration.js`


Implementation
--------------

This project is written in JavaScript using React, with Kotlin for the TeamCity plugin backend.


Possible Future Features
------------------------

* Make required build properties configurable.

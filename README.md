TeamCity Deployment Dashboard
=============================

A web app to query the TeamCity REST API for deployment builds and show what has been deployed in what environment.


Getting Started
---------------

1. Download and install latest release into any web server and note the deployment location
2. Update `configuration.js` with the TeamCity base URL
3. Update `configuration.js` with the environment names in the order they should be shown
4. Configure TeamCity to accept CORS requests:
  * Go to Administration / Diagnostics / Internal Properties
  * Permit access from dashboard: `rest.cors.origins=http://<deployment host/port>`
6. Ensure you are logged in to TeamCity
7. Visit deployment location in the same browser, your credentials will be used to make the REST requests


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

This project is written in JavaScript using React.

The other purpose of this dashboard is to allow me to learn React and modern JavaScript, so the implementation is not
likely to be a good example of those technologies. Prop-drilling rather Context/Redux, old-style function syntax, etc.


Possible Future Features
------------------------

* Make required build properties configurable.
* Make projects and deployments clickable to easily take to relevant TeamCity pages.
* TeamCity plugin that embeds the dashboard into TeamCity itself, such as a project tab showing
  all deployments at that hierarchy level and below

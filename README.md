# register-country-by-country-reporting-frontend

## Info

This is a ROSM registration frontend which allows a user (agent, client, or organisation) to register & subscribe to Country By Country Reporting.

The service has a backend ([register-country-by-country-reporting](https://github.com/hmrc/country-by-country-reporting)) which integrates with ETMP for registrations and subscriptions.

Once a user has registered and subscribed to CBC they can then use the [County by Country Reporting Frontend](https://github.com/hmrc/country-by-country-reporting-frontend) to submit CBC reports to HMRC.

### Dependencies

| Service                                       | Link                                                                    |
|-----------------------------------------------|-------------------------------------------------------------------------| 
| Address Lookup                                | https://github.com/hmrc/address-lookup                                  |
| Email                                         | https://github.com/hmrc/email                                           |
| Auth                                          | https://github.com/hmrc/auth                                            |
| Tax Enrolments                                | https://github.com/hmrc/tax-enrolments                                  |
| Register country by country reporting         | https://github.com/hmrc/register-country-by-country-reporting           |
| Register country by country reporting stubs   | https://github.com/hmrc/register-country-by-country-reporting-stubs     |

### Endpoints used

| Service                                | HTTP Method | Route                                                                         | Purpose                                                                                                                                                   |
|----------------------------------------|-------------|-------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| Tax Enrolments                         | POST        | /tax-enrolments/service/:serviceName/enrolment                                | Add the CBC enrolment to the user's account so that they can use the [reporting frontend](https://github.com/hmrc/country-by-country-reporting-frontend). |
| Register-country-by-country-reporting  | POST        | /register-country-by-country-reporting/registration/noId                      | Register the user without an id (e.g. corporation tax UTR)                                                                                                |
| Register-country-by-country-reporting  | POST        | /register-country-by-country-reporting/registration/utr                       | Register the user using an id (e.g. corporation tax UTR)                                                                                                  |
| Register-country-by-country-reporting` | POST        | /register-country-by-country-reporting/subscription/create-subscription       | Subscribe the user to Country by Country Reporting                                                                                                        |
| Register-country-by-country-reporting  | POST        | /register-country-by-country-reporting/subscription/read-subscription/:safeId | Check if a user is already subscribed to Country by Country Reporting                                                                                     |
| Email                                  | POST        | /hmrc/email                                                                   | Send an email to the user to confirm they have subscribed to Country by Country Reporting                                                                 |
| Enrolment store proxy                  | POST        | /enrolment-store-proxy/enrolments/:enrolmentKey/groups                        | Get the user's enrolment information to check if they have an existing Country by Country Reporting enrolment                                             |

## Running the service

Service Manager: CBCR_NEW_ALL

Port: 10026

Link: http://localhost:10026/register-to-send-a-country-by-country-report

Affinity Group: Organisation

## Tests and prototype

[View the prototype here](https://cbcr-prototype.herokuapp.com/)

[View the journey tests here](https://github.com/hmrc/register-country-by-country-reporting-ui-tests)




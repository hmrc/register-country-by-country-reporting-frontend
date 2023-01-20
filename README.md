# register-country-by-country-reporting-frontend

## Info

This service allows a user(agent, client or organisation) to register as a CBCR administrator.

This service has a corresponding back-end service, namely register-country-by-country-reporting can be viewed [here](https://github.com/hmrc/register-country-by-country-reporting) which integrates with HOD, i.e. DES/ETMP.

### Dependencies

| Service          | Link                                                                   |
|------------------|------------------------------------------------------------------------| 
| Address Lookup   | https://github.com/hmrc/address-lookup                                 |
| Email            | https://github.com/hmrc/email                                          |
| Auth             | https://github.com/hmrc/auth                                           |
| Tax Enrolments   | https://github.com/hmrc/tax-enrolments                                 |

### Origin

https://github.com/hmrc/register-country-by-country-reporting-frontend 

### Endpoints used

| Service              | HTTP Method | Route                                                                         | Purpose                                                          |
|----------------------|-------------|-------------------------------------------------------------------------------|------------------------------------------------------------------|
| Tax Enrolments       | POST        | /tax-enrolments/service/:serviceName/enrolment                                | Enrols a user synchronously for a given service name             
| Registration         | POST        | /register-country-by-country-reporting/registration/noId                      | Enables user to register witout id                                                                        
| Registration         | POST        | /register-country-by-country-reporting/registration/utr                       | Enables user to register                                                                        
| Subscription`        | POST        | /register-country-by-country-reporting/subscription/create-subscription       | Enables user to create subscription                                                                     
| Subscription         | POST        | /register-country-by-country-reporting/subscription/read-subscription/:safeId | Enables user to read subscription details                                                                        
| Email                | POST        | /hmrc/email                                                                   | Sends an email to an email address                                                                        

## Running the service

Service Manager: CBCR_NEW_ALL

Port: 10026

Link: http://localhost:10026/register-to-send-a-country-by-country-report/register/have-utr

## Tests and prototype

[View the prototype here](https://cbc-reporting-prototype.herokuapp.com/)

[View the journey tests here](https://github.com/hmrc/register-country-by-country-reporting-ui-tests)




To verify the results of a BDD test (as found in data tables in the various .feature files), we check 3 columns - the response object, the key of the property to test and the value expected.

Please check the StepAssertor class to see how the response object are identified, and also how the keys are mapped to json (in KEY_MAP).

Please see the StepAssertorTest class to see the StepAssertor in action.

IMPORTANT - if the response format from the service changes, update the json in the StepAssertor to an example in the new format.  Then fix all the tests it breaks.

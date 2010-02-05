@echo off
REM Loads Value Domain content, provided in LexGrid canonical xml format.
REM
REM Options:
REM   -in,--input <uri> URI or path specifying location of the source file.
REM   -v, --validate <int> Perform validation of the candidate
REM         resource without loading data.  
REM         Supported levels of validation include:
REM         0 = Verify document is well-formed
REM         1 = Verify document is valid
REM Example: LoadValueDomain -in "file:///path/to/file.xml"
REM
java -Xmx1300m -cp "..\runtime\lbPatch.jar;..\runtime\lbRuntime.jar" org.lexgrid.valuedomain.admin.LoadValueDomain %*
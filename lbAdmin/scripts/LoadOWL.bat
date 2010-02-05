@echo off
REM Loads an OWL file. You can provide a manifest file to configure coding scheme
REM meta data.
REM
REM Options:
REM   -in,--input <uri> URI or path specifying location of the source file
REM   -mf,--manifest <uri> URI or path specifying location of the manifest file
REM   -ms,--memorySetting <int> If specified, indicates the profile
REM         used to tune memory/performance tradeoffs. Options are:
REM         1 = Faster/more memory (holds OWL in memory)
REM         2 = Slower/less memory (cache OWL to database)
REM   -v, --validate <int> Perform validation of the candidate
REM         resource without loading data.  If specified, the '-a' and '-t'
REM         options are ignored.  Supported levels of validation include:
REM         0 = Verify document is well-formed
REM         1 = Verify document is valid
REM   -a, --activate ActivateScheme on successful load; if unspecified the vocabulary is loaded but not activated
REM   -t, --tag <id> An optional tag ID (e.g. 'PRODUCTION' or 'TEST') to assign. 
REM
REM Example: LoadOWL -in "file:///path/to/somefile.owl" -a
REM          LoadOWL -in "file:///path/to/somefile.owl" -v 0
REM
java -Xmx1000m -XX:MaxPermSize=256M -cp "..\runtime\lbPatch.jar;..\runtime\lbRuntime.jar" org.LexGrid.LexBIG.admin.LoadOWL %*
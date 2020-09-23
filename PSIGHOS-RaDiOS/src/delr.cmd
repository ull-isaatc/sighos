for /f "tokens=* delims=" %%i in ('dir /s /b *.class') do (
del "%%i"
)
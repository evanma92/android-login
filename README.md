# Description of Android-Login App

## The register button:

1. On click, the app gets the username and password and puts them in a string
2. Then we check whether the user has input his/her username
	+ If not, we ask the user to input his/her username (using a snackbar)
3. Then we check whether the password is of length 8 or more
	+ If not, we ask the user to input again (using a snackbar)
4. If all the above conditions are met, we save the userdata
5. Start the other activity and display "Registration Successful!"

### Saving the userdata:

1. Opens the file output using a standard filename "UserData"
2. Get the bytes from the username and password
3. Writes the hashed password and username to the file
4. Closes the file

## The login button:

1. On click, the app gets the username and password and puts them in a string
2. Then we hash it to get the hashed password (hash_pw) - using *md5*
3. Next we read the data from file
4. We then check the username and password accordingly by
	+ Splitting up the data into two components
	+ Checking for equality
5. If login is successful, then we create a new intent and launch a new activity
called DisplayMessageActivity, which displays a welcome message to the user
	+ Else we ask the user to register or input their username/password again
	  by calling another intent.

### Reading the userdata:

1. Opens the file output
2. Uses a readerBuffer to get the bytes and store them
3. Returns a string for string comparison

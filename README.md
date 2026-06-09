### Setup

The database is a postgresql database and requires the vector extension to be installed.
* in the terminal run `sudo apt install postgresql-16-pgvector`
    * Check your postgresql version, if not 16 change the command to the major version you have
* Create your database in psql thenrun the following command to install the vector extension. 
  * `CREATE EXTENSION IF NOT EXISTS vector`

Then run the app for the first time and JPA will create a table within your database.
* Database url, password, and username are set in the properties.

Use Postman or some other rest api service to test. When finished testing change or remove `spring.servlet.multipart.max-file-size=5MB`
from the properties.

### Testing with Postman 
* `/identify` will take `imageFile` as a key

Pokemon cards are about 2.5 inches wide and 3.5 inches long
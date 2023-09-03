# GitHub Repository Viewer

GitHub Repository Viewer is a simple Java Spring Boot application that allows you to retrieve and view GitHub
repositories for a given user.

## Features

- Retrieve a list of GitHub repositories for a specific user.
- View details of each repository, including branches.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) installed (Java 8 or higher).
- Apache Maven installed.
- GitHub Personal Access Token (used for authentication with the GitHub API).

## Getting Started

### Setup GitHub Authentication

To authenticate with the GitHub API, you'll need to set up a Personal Access Token:

1. Go to your GitHub Settings.
2. Navigate to "Developer settings".
3. Click on "Personal access tokens".
4. Generate a new token with the necessary permissions.
5. Copy the generated token.

1. Now, place the token in the application.properties file:
    ```bash
    github.api.token=YOUR_GITHUB_PERSONAL_ACCESS_TOKEN

Replace YOUR_GITHUB_PERSONAL_ACCESS_TOKEN with the token you copied.

## Running the Application

1. Clone this repository to your local machine:
   ```bash
   git clone https://github.com/zephir0/Github-Repository-Viewer.git

2. Navigate to the project directory:
    ```bash
    cd Github-Repository-Viewer

3. Build the application using Maven:
    ```bash
    mvn clean install

4. Run the Spring Boot application:
    ```bash
    java -jar target/github-repository-viewer-1.0.jar

The application should now be running locally.

## Usage

To use the GitHub Repository Viewer, you can make HTTP GET requests to the following endpoint:

GET /api/repositories/{username}: Retrieve a list of GitHub repositories for the specified GitHub username.

1. Example request:
      ```bash
      curl http://localhost:8081/api/repositories/{username}

Replace {username} with the GitHub username you want to retrieve repositories for.

## Error Handling

### User Not Found

If the specified GitHub user does not exist, the API will return a `404 NOT_FOUND` response. This means that the
requested user profile was not found on GitHub.

### Other Errors

For any other errors, appropriate error responses will be returned.

### Content Type Validation

The API expects requests to have the `Content-Type` header set to `application/json`. If the headers are not set
to `application/json`, the API will return a `406 NOT_ACCEPTABLE` response, indicating that the request's media type is
not supported.

Example of a valid request header:

```http
Content-Type: application/json

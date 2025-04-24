public class GitRepoCreator {

    public void createRepository(String name) {
        // Simulate request parameters
        class RequestParams {
            private final java.util.Map<String, Object> params = new java.util.HashMap<>();
            public void addParam(String key, Object value) { params.put(key, value); }
            public Object getParam(String key) { return params.get(key); }
        }

        // Simulate GitHub API client
        class GitHubApiClient {
            public void createRepo(RequestParams params) {
                String repoName = (String) params.getParam("name");
                System.out.println("Repository created: https://github.com/user/" + repoName);
            }
        }

        RequestParams requestParams = new RequestParams();
        requestParams.addParam("name", name);

        GitHubApiClient gitHubApiClient = new GitHubApiClient();
        gitHubApiClient.createRepo(requestParams);
    }
}
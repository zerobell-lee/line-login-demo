package xyz.zerobell.linelogin;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OAuthController {

    private final String clientId = "1569797740";
    private final String clientSecret = "72c611a7804a3a3c5e838b656dbdbe94";
    private List<AccessToken> tokenList = new ArrayList<>();

    @GetMapping("auth")
    public void getAuth(@RequestParam String code, @RequestParam String state) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("grant_type", "authorization_code");
        parameters.add("code", code);
        parameters.add("redirect_uri", "https://lyj-line-login-demo.herokuapp.com/tokenList");
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        AccessToken token = restTemplate.postForObject("https://api.line.me/oauth2/v2.1/token", request, AccessToken.class);
        tokenList.add(token);
    }

    @GetMapping("tokenList")
    public List<AccessToken> showTokenList() {
        return tokenList;
    }
}
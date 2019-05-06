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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OAuthController {

    private final String clientId = "input client Id";
    private final String clientSecret = "input client secret";
    private final String host = System.getenv("host");
    private List<AccessToken> tokenList = new ArrayList<>();

    @GetMapping("auth")
    public RedirectView getAuth(@RequestParam String code, @RequestParam String state, RedirectAttributes attributes) {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("grant_type", "authorization_code");
        parameters.add("code", code);
        parameters.add("redirect_uri", host + "/auth");
        parameters.add("client_id", clientId);
        parameters.add("client_secret", clientSecret);

        /*
        * POST https://api.line.me/oauth2/v2.1/token 1.1
        * application/x-www-form-urlencoded
        *
        * grant_type=authorization_code
        * code={code}
        * redirect_uri={redirect_uri}
        * client_id=
        * client_secret=
        *
        *  */

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        AccessToken token = restTemplate.postForObject("https://api.line.me/oauth2/v2.1/token", request, AccessToken.class);
        tokenList.add(token);

        return new RedirectView("success");
    }

    @GetMapping("tokenList")
    public List<AccessToken> showTokenList() {
        return tokenList;
    }

    @GetMapping("view/{id}")
    public Profile view(@PathVariable long id) {
        AccessToken token = tokenList.get((int) id);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token.getAccessToken());

        /*
        * GET https://api.line.me/v2/profile 1.1
        * Authorization: Bearer {token}
        *
        *  */

        HttpEntity<String> request = new HttpEntity<>("", headers);

        Profile profile = restTemplate.postForObject("https://api.line.me/v2/profile", headers, Profile.class);

        return profile;

    }
}
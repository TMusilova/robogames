package com.robogames.RoboCupMS.Security;

import com.robogames.RoboCupMS.GlobalConfig;
import com.robogames.RoboCupMS.Response;
import com.robogames.RoboCupMS.ResponseHandler;
import com.robogames.RoboCupMS.Business.Security.AuthService;
import com.robogames.RoboCupMS.Business.Security.LoginObj;
import com.robogames.RoboCupMS.Business.Security.RegistrationObj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(GlobalConfig.AUTH_PREFIX)
public class AuthControler {

    @Autowired
    private AuthService authService;

    /**
     * Prihlaseni uzivatele do systemu (pokud je email a heslo spravne tak
     * vygeneruje, navrati a zapise do databaze pristupovy token pro tohoto
     * uzivatele). Token se stava automaticky neplatnym po uplynuti
     * definovaneho casu "GlobalConfig.TOKEN_VALIDITY_DURATION".
     * 
     * @param email    Email uzivatele
     * @param password Heslo uzivatele
     * @return Pristupovy token
     */
    @PostMapping("/login")
    public Response login(@RequestBody LoginObj loginObj) {
        try {
            String token = this.authService.login(loginObj);
            return ResponseHandler.response(token);
        } catch (Exception ex) {
            return ResponseHandler.error(ex.getMessage());
        }
    }

    /**
     * Vygeneruje odkaz pro autorizaci a autentizaci uzivatele. Po uspesne
     * autorizaci je uzivatel presmerovan na specifikovanou adresu. Zde musi byt
     * odeslan POST request na endpoint serveru "/auth/oAuth2GenerateToken" s
     * parametrem "code" a "redirectURI" jejihz hodnuty ziska aktualni URL.
     * "redirectURI" bude predan parametrem "state".
     * 
     * @param redirectURI Presmerovani na URL "frond-end" -> pres JS predat
     *                    autorizacni kod serveru "oAuth2GenerateToken
     * @return URL odkaz pro autorizaci a autentizaci uzivatele
     * @throws Exception
     */
    @GetMapping("/oAuth2")
    public Response getOAuth2URI(
            @RequestParam(defaultValue = "https://localhost/auth/oauth2/code") String redirectURI) {
        try {
            String url = this.authService.getOAuth2URI(redirectURI);
            return ResponseHandler.response(url);
        } catch (Exception ex) {
            return ResponseHandler.error(ex.getMessage());
        }
    }

    /**
     * Vygeneruje pristupovy token pro uzivatele s vyuzitim OAuth2 autorizacniho
     * kodu
     * 
     * @param redirectURI URI presmerovani, zistka paramatru state spolu s
     *                    autorizacnim kodem
     * @param code        Autorizacni kod pro klienta
     * @return Pristupovy token uzivatele
     */
    @PostMapping("/oAuth2GenerateToken")
    public Response oAuth2GenerateToken(@RequestParam String redirectURI, @RequestParam String code) {
        try {
            String url = this.authService.oAuth2GenerateToken(redirectURI, code);
            return ResponseHandler.response(url);
        } catch (Exception ex) {
            return ResponseHandler.error(ex.getMessage());
        }
    }

    /**
     * Odhlasi uzivatele ze systemu (odstrani pristupovy token z databaze)
     * 
     * @param email Email uzivatele
     * @return Status
     */
    @PostMapping("/logout")
    public Response logout() {
        try {
            this.authService.logout();
            return ResponseHandler.response("success");
        } catch (Exception ex) {
            return ResponseHandler.error(ex.getMessage());
        }
    }

    /**
     * Registruje noveho uzivatele
     * 
     * @param newUser Registracni udaje noveho uzivatele
     * @return Nove vytvoreni uzivatel
     */
    @PostMapping("/register")
    public Response register(@RequestBody RegistrationObj newUser) {
        try {
            this.authService.register(newUser);
            return ResponseHandler.response("success");
        } catch (Exception ex) {
            return ResponseHandler.error(ex.getMessage());
        }
    }

}

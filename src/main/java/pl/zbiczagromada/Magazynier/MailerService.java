package pl.zbiczagromada.Magazynier;

import lombok.Getter;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.AsyncResponse;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.forgotpasswordcode.ForgotPasswordCode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class MailerService {
      @Value("${smtp.host}")
      private String smtpHost = "localhost";

      @Value("${smtp.port}")
      private Integer smtpPort = 25;

      @Value("${smtp.user}")
      private String smtpUser = "";

      @Value("${smtp.password}")
      private String smtpPassword = "";

      @Value("${smtp.transport}")
      private TransportStrategy smtpTransport = TransportStrategy.SMTP;

      @Value("${smtp.from}")
      private String smtpFrom = "no-reply@magazynier.local";

      @Value("${smtp.baseurl}")
      private String baseUrl = "";

      Logger log = LoggerFactory.getLogger(MailerService.class);

      @Getter(lazy = true)
      private final Mailer mailer = MailerBuilder
            .withSMTPServer(smtpHost, smtpPort, smtpUser, smtpPassword)
            .withTransportStrategy(smtpTransport)
            .withSessionTimeout(10 * 1000)
            .buildMailer();

      public void sendAccountVerifyEmail(User user, String password){
            String greeting = "";
            if(user.getDisplayname() != null && !user.getDisplayname().isEmpty()) greeting = "Cześć " + user.getDisplayname() + "!<br>";
            String link = baseUrl + "usermanagement.html?action=activate&username=" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8) + "&code=" + URLEncoder.encode(user.getActivationCode().getCode(), StandardCharsets.UTF_8);
            Email email = EmailBuilder.startingBlank()
                    .to(user.getUsername(), user.getEmail())
                    .from("Magazynier", smtpFrom)
                    .withSubject("Potwierdź rejestrację")
                    .withHTMLText(greeting + "Twoje konto w Magazynierze właśnie zostało utworzone. Aby je aktywować, kliknij <a href='" + link + "'>w ten link</a>.<br>" +
                    "Twój login to <b>" + user.getUsername() + "</b>, a hasło to <b>" + password + "</b>. Zalecamy zmienić je po pierwszym zalogowaniu.")
                    .withPlainText("Twój login to " + user.getUsername() + " a hasło to " + password + " -> aby aktywować swoje konto, otwórz ten link w przeglądarce: " + link)
                    .buildEmail();

            AsyncResponse mailExecutionStatus = getMailer().sendMail(email, true);
            mailExecutionStatus.onException((e) -> {
                  log.error("Couldn't send account verification e-mail to " + user.getUsername() + " (" + user.getEmail() + ")");
                  e.printStackTrace();
            });
      }

      public void sendForgotPasswordEmail(User user, ForgotPasswordCode code){
            String greeting = "";
            if(user.getDisplayname() != null && !user.getDisplayname().isEmpty()) greeting = "Cześć " + user.getDisplayname() + "!<br>";
            String link = baseUrl + "usermanagement.html?action=resetpassword&code=" + URLEncoder.encode(code.getCode(), StandardCharsets.UTF_8);
            Email email = EmailBuilder.startingBlank()
                    .to(user.getUsername(), user.getEmail())
                    .from("Magazynier", smtpFrom)
                    .withSubject("Resetowanie hasła")
                    .withHTMLText(greeting + "Aby zresetować swoje hasło w Magazynierze, kliknij <a href='" + link + "'>w ten link</a>. Będzie on aktywny przez 24 godziny.")
                    .withPlainText("Aby zresetować swoje hasło, otwórz ten link w przeglądarce: " + link + "\nLink będzie aktywny 24 godziny.")
                    .buildEmail();

            AsyncResponse mailExecutionStatus = getMailer().sendMail(email, true);
            mailExecutionStatus.onException((e) -> {
                  log.error("Couldn't send forgot password e-mail to " + user.getUsername() + " (" + user.getEmail() + ")");
                  e.printStackTrace();
            });
      }

      public void sendPasswordChangedEmail(User user){
            String greeting = "";
            if(user.getDisplayname() != null && !user.getDisplayname().isEmpty()) greeting = "Cześć " + user.getDisplayname() + "!<br>";

            Email email = EmailBuilder.startingBlank()
                    .to(user.getUsername(), user.getEmail())
                    .from("Magazynier", smtpFrom)
                    .withSubject("Hasło zostało zresetowane")
                    .withHTMLText(greeting + "Twoje hasło w Magazynierze zostało zresetowane.")
                    .withPlainText("Twoje hasło w Magazynierze zostało zresetowane.")
                    .buildEmail();

            AsyncResponse mailExecutionStatus = getMailer().sendMail(email, true);
            mailExecutionStatus.onException((e) -> {
                  log.error("Couldn't send password changed e-mail to " + user.getUsername() + " (" + user.getEmail() + ")");
                  e.printStackTrace();
            });
      }

      public void sendPermissionGroupChangedEmail(User user){
            String greeting = "";
            if(user.getDisplayname() != null && !user.getDisplayname().isEmpty()) greeting = "Cześć " + user.getDisplayname() + "!<br>";

            Email email = EmailBuilder.startingBlank()
                    .to(user.getUsername(), user.getEmail())
                    .from("Magazynier", smtpFrom)
                    .withSubject("Masz nowe uprawnienia")
                    .withHTMLText(greeting + "Otrzymałeś właśnie nowy poziom uprawnień w Magazynierze. Od teraz twoja grupa to <b>" + user.getPermissionGroup() + "</b>.")
                    .withPlainText("Otrzymałeś właśnie nowy poziom uprawnień w Magazynierze. Od teraz twoja grupa to " + user.getPermissionGroup())
                    .buildEmail();

            AsyncResponse mailExecutionStatus = getMailer().sendMail(email, true);
            mailExecutionStatus.onException((e) -> {
                  log.error("Couldn't send permission group changed e-mail to " + user.getUsername() + " (" + user.getEmail() + ")");
                  e.printStackTrace();
            });
      }
}

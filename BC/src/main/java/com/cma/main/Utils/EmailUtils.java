package com.cma.main.Utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String body, List<String> adminsEmail) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("therealnannyskitchen@gmail.com");
        mail.setSubject(subject);
        mail.setTo(to);
        mail.setText(body);

//        Since adminsEmail is of type List<String>, we cannot add it directly here. So we are converting it to a String array in the getCcEmails method below

        if (adminsEmail != null && adminsEmail.size() > 0) {
            mail.setCc(getCcEmails(adminsEmail));
        }

        javaMailSender.send(mail);
    }


    private String[] getCcEmails(List<String> adminsEmail) {
        String[] cc = new String[adminsEmail.size()];

        for (int i = 0; i < adminsEmail.size(); i++) {
            cc[i] = adminsEmail.get(i);
        }
        return cc;
    }

    public void sendPassword(String to, String subject, String password) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("therealnannyskitchen@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String Msge = "<body>&nbsp;&nbsp; WELCOME TO <strong>The Nanny&#39;s Kitchen</strong> &nbsp;<div>&nbsp;</div><div>&nbsp; &nbsp;Please check the below login details,</div><div>&nbsp;</div><div>&nbsp; &nbsp;Email -&nbsp; " + to + " </div><div>&nbsp; &nbsp;Password -&nbsp; " + password + "</div><div><br>&nbsp; &nbsp;Please don&#39;t share the above details with anyone.</div> <div><br>&nbsp; &nbsp;Have a nice day!!!</div></body>";

        message.setContent(Msge, "text/html");

        javaMailSender.send(message);

    }

}

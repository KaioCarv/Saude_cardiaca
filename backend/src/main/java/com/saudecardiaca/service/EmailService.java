package com.saudecardiaca.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetCode(String toEmail, String firstName, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Redefinição de Senha - Saúde Cardíaca");
            helper.setText(buildEmailHtml(firstName, code), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar e-mail. Tente novamente.", e);
        }
    }

    private String buildEmailHtml(String firstName, String code) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <style>
                body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
                .container { max-width: 480px; margin: 40px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 12px rgba(0,0,0,0.1); }
                .header { background: linear-gradient(135deg, #e53935, #c62828); padding: 32px; text-align: center; }
                .header h1 { color: #fff; margin: 0; font-size: 22px; }
                .header p { color: rgba(255,255,255,0.85); margin: 4px 0 0; font-size: 14px; }
                .body { padding: 32px; }
                .body p { color: #444; font-size: 15px; line-height: 1.6; }
                .code-box { background: #fce4e4; border: 2px dashed #e53935; border-radius: 10px; text-align: center; padding: 20px; margin: 24px 0; }
                .code-box span { font-size: 36px; font-weight: bold; letter-spacing: 10px; color: #c62828; font-family: monospace; }
                .footer { background: #f9f9f9; padding: 16px 32px; text-align: center; }
                .footer p { color: #999; font-size: 12px; margin: 0; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <h1>❤️ Saúde Cardíaca</h1>
                  <p>Redefinição de Senha</p>
                </div>
                <div class="body">
                  <p>Olá, <strong>%s</strong>!</p>
                  <p>Recebemos uma solicitação para redefinir a senha da sua conta. Use o código abaixo:</p>
                  <div class="code-box">
                    <span>%s</span>
                  </div>
                  <p>Este código expira em <strong>15 minutos</strong>. Se você não solicitou a redefinição de senha, ignore este e-mail.</p>
                </div>
                <div class="footer">
                  <p>© 2025 Saúde Cardíaca. Este é um e-mail automático, não responda.</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(firstName, code);
    }
}

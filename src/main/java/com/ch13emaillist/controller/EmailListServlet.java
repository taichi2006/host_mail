package com.ch13emaillist.controller;

import java.io.*;

import com.ch13emaillist.Util.MailUtil;
import com.ch13emaillist.model.User;
import com.ch13emaillist.model.UserDB;
import jakarta.mail.MessagingException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/emailList")
public class EmailListServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String url = "/index.jsp";
        String message = "";

        // get current action
        String action = request.getParameter("action");
        if (action == null) {
            action = "join"; // default action
        }

        // perform action and set URL to appropriate page
        if (action.equals("join")) {
            url = "/index.jsp"; // the "join" page
        } else if (action.equals("add")) {
            // get parameters from the request
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");

            // store data in User object
            User user = new User(firstName, lastName, email);
            ClassLoader loader = Thread.currentThread().getContextClassLoader();

            if (UserDB.emailExists(user.getEmail())) {
                // TRƯỜNG HỢP 1: EMAIL ĐÃ TỒN TẠI
                message = "This email address already exists.<br>" +
                        "Please enter another email address.";
                url = "/index.jsp";
            } else {
                // TRƯỜNG HỢP 2: EMAIL HỢP LỆ VÀ CHƯA TỒN TẠI
                message = "";
                url = "/thanks.jsp";

                // Lưu vào cơ sở dữ liệu
                UserDB.insert(user);

                // ----- BẮT ĐẦU QUÁ TRÌNH GỬI MAIL -----
                String to = email;
                String from = "haohan123ajaja@gmail.com";
                String subject = "Welcome to our email list";
                String body = "Dear " + firstName + ",\n\n"
                        + "Thanks for joining our email list. "
                        + "We'll make sure to send "
                        + "you announcements about new products "
                        + "and promotions.\n"
                        + "Have a great day and thanks again!\n\n"
                        + "Kelly Slivkoff\n"
                        + "Mike Murach & Associates";
                boolean isBodyHTML = false;

                try {
                    MailUtil.sendMail(to, from, subject, body, isBodyHTML);

                } catch (MessagingException e) {
                    String errorMessage
                            = "ERROR: Unable to send email. "
                            + "Check Tomcat logs for details.<br>"
                            + "NOTE: You may need to configure your system "
                            + "as described in chapter 14.<br>"
                            + "ERROR MESSAGE: " + e.getMessage();

                    // Nếu gửi mail lỗi, bạn có thể cân nhắc gán thông báo lỗi vào message
                    // và vẫn cho phép lưu dữ liệu hoặc rollback tùy nghiệp vụ,
                    // ở đây ta sẽ in lỗi lên màn hình cho người dùng biết.
                    request.setAttribute("errorMessage", errorMessage);
                    this.log(
                            "Unable to send email. \n"
                                    + "Here is the email you tried to send: \n"
                                    + "=====================================\n"
                                    + "TO: " + email + "\n"
                                    + "FROM: " + from + "\n"
                                    + "SUBJECT: " + subject + "\n\n"
                                    + body + "\n\n");
                }
                // ----- KẾT THÚC QUÁ TRÌNH GỬI MAIL -----
            }

            // Đẩy dữ liệu sang view (JSP)
            request.setAttribute("user", user);
            request.setAttribute("message", message);
        }

        // Chuyển hướng người dùng tới trang đích cuối cùng
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }
}
package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import models.Book;
import models.Genre;
import models.User;

@WebServlet("/save-book.do")
public class SaveBookServlet extends HttpServlet{
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();

        User user = (User)session.getAttribute("user");
        Book book = new Book();

        if(ServletFileUpload.isMultipartContent(request)) {
            ServletContext context = getServletContext();

            try {
                List<FileItem> list = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

                String uploadPath = context.getRealPath("/WEB-INF/uploads/" + user.getEmail() + "/book_pics");
                System.out.println(uploadPath);

                for(FileItem item : list) {
                    if(!item.isFormField()) {
                        File file = new File(uploadPath, item.getName());

                        try {
                            item.write(file);
                            book.setBookImg(item.getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if(item.getFieldName().equals("title")) {
                            book.setTitle(item.getString());
                        } else if(item.getFieldName().equals("description")) {
                            book.setDescription(item.getString());
                        } else if (item.getFieldName().equals("price")) {
                            book.setPrice(Integer.parseInt(item.getString()));
                        } else if (item.getFieldName().equals("book-genre")) {
                            book.setGenre(new Genre(Integer.parseInt(item.getString())));
                        }

                        book.setAuthor(user.getName());
                        book.setUser(user);
                    }
                }
            } catch(FileUploadException e) {
                e.printStackTrace();
            }

        }

        if(book.saveBook()) {
            request.getRequestDispatcher("profile.do").forward(request, response);
        }
    }
}

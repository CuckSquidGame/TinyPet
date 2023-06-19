package foo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@WebServlet(name = "PetServlet", urlPatterns = { "/petition" })
public class PetitionServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		Random r = new Random();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        int nbPet = 150;
        int nbUsers = 200;

		// Create petition
		for (int i = 0; i < nbPet; i++) {
			Entity e = new Entity("Pétition", "P" + i );
			int owner=r.nextInt(nbUsers);
            e.setProperty("Nom", "Name" + i);
			e.setProperty("Propriétaire", "U"+ owner);
			e.setProperty("Date", new Date());
			e.setProperty("Body", "bla bla bla je suis une pétition");			
			HashSet<String> signataires = new HashSet<String>();
            signataires.add("U"+owner);
            for(int j=0; j< r.nextInt(10);j++){
                signataires.add("U"+j);
            }
            
            e.setProperty("Signataires",signataires);
			e.setProperty("nbSignatures", signataires.size());

			// Create random tags
			HashSet<String> ftags = new HashSet<String>();
			while (ftags.size() < 10) {
				ftags.add("T" + r.nextInt(100));
			}
			e.setProperty("Tags", ftags);
			
			response.getWriter().print("<li> created post:" + e.getKey() + "<br>");
            datastore.put(e);
		}

   
	}
}
package foo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.api.server.spi.auth.EspAuthenticator;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.repackaged.com.google.datastore.v1.PropertyFilter.Operator;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;

@Api(name = "myApi",
     version = "v1",
     audiences = "677536920381-7cu23ijjgimgqpoh4kpjhfj8997ngkq8.apps.googleusercontent.com",
  	 clientIds = {"677536920381-7cu23ijjgimgqpoh4kpjhfj8997ngkq8.apps.googleusercontent.com",
        "677536920381-7cu23ijjgimgqpoh4kpjhfj8997ngkq8.apps.googleusercontent.com"},
     namespace =
     @ApiNamespace(
		   ownerDomain = "helloworld.example.com",
		   ownerName = "helloworld.example.com",
		   packagePath = "")
     )

public class PetitionEndpoint {
    
    // renvoie les 100 pétitions les plus signées, par date décroissante
    @ApiMethod(name = "petitions", httpMethod = HttpMethod.GET)
	public List<Entity> petitions() {
		Query q = new Query("Pétition").addSort("nbSignatures", SortDirection.DESCENDING).addSort("Date",SortDirection.DESCENDING);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();        
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(100));
		return result;
	}

    // renvoie les pétitions qui ont été signés par un utilisateur
    @ApiMethod(name = "petitionsByUser", httpMethod = HttpMethod.GET)
	public List<Entity> petitionsByUser(@Named("email") String email) {
		Query q = new Query("Pétition").setFilter(new FilterPredicate("Signataires", FilterOperator.EQUAL, email)).addSort("Date",SortDirection.DESCENDING);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(20));
		return result;
	}

    // crée une pétition
    @ApiMethod(name = "postPetition", httpMethod = HttpMethod.POST)
	public Entity postPetition(@Named("full") String full, @Named("email") String email, @Named("name") String name,@Named("body") String body, @Named("tags") List<String> tags) throws EntityNotFoundException{

        
        List<String> signataires = new ArrayList<String>();
        signataires.add(email);

		Entity e = new Entity("Pétition");
		e.setProperty("Propriétaire", full);
		e.setProperty("Nom", name);
		e.setProperty("Body", body);
		e.setProperty("Date", new Date());
        e.setProperty("Signataires", signataires);
        e.setProperty("nbSignatures", 1);
        e.setProperty("Tags", tags);
		
        
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
        datastore.put(e);
		txn.commit();

		return e;
        
	}

    // signature d'une pétition, renvoie la pétition si ca a marché, rien si c'est déjà signé
    @ApiMethod(name = "signPetition", httpMethod = HttpMethod.POST)
	public Entity signPetition(@Named("name") String name, @Named("email") String email) {
        Query q = new Query("Pétition").setFilter(new FilterPredicate("Nom", FilterOperator.EQUAL, name));
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
        Entity result = pq.asSingleEntity();
        ArrayList<String> signataires = (ArrayList<String>) result.getProperty("Signataires");
        //System.out.println(signataires.size());
        
        if(!signataires.contains(email)){
            signataires.add(email);
            result.setProperty("nbSignatures", signataires.size());

            Transaction txn = datastore.beginTransaction();
		    datastore.put(result);
		    txn.commit();
        
            return result;
        }
        else{
            return null;    
        }
      
    }

    // renvoie les pétitions qui ont le nom recherché
    @ApiMethod(name = "petitionsByName", httpMethod = HttpMethod.GET)
	public List<Entity> petitionsByName(@Named("name") String name) {
		Query q = new Query("Pétition").setFilter(new FilterPredicate("Nom", FilterOperator.EQUAL, name));
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(20));
		return result;
	}

    // renvoie les pétitions qui ont le nom recherché
    @ApiMethod(name = "petitionsByTag", httpMethod = HttpMethod.GET)
	public List<Entity> petitionsByTag(@Named("tag") String tag) {
		Query q = new Query("Pétition").setFilter(new FilterPredicate("Tags", FilterOperator.EQUAL, tag));
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);
		List<Entity> result = pq.asList(FetchOptions.Builder.withLimit(20));
		return result;
	}




}

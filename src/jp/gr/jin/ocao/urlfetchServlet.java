package jp.gr.jin.ocao;

import java.io.IOException;
import javax.servlet.http.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jdo.PersistenceManager;
import javax.jdo.JDOObjectNotFoundException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

//import java.io.IOException;

@SuppressWarnings("serial")
public class urlfetchServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		// URL url = new URL("http://www.example.com/atom.xml");
		// URL url = new URL("http://rss.cnn.com/rss/edition.rss");
		// URL url = new
		// URL("http://feeds2.feedburner.com/AbcNews_TopStories?format=xml");
		URL url = new URL("http://b137053.ppp.asahi-net.or.jp/~kgbu/partial.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				url.openStream(), "UTF-8"));
		String line;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {

			while ((line = reader.readLine()) != null) {
				String[] entry = line.split("\t");
				doregist(pm, entry[0], entry[1], entry[2], entry[3]);
				resp.getWriter().println(line);
			}

		} catch (MalformedURLException e) {
			resp.getWriter().println("URL指定エラー");
		} catch (IOException e) {
			resp.getWriter().println("IOエラー");
		} finally {
			reader.close();
			pm.close();
		}
	}

	private void doregist(PersistenceManager pm, String prefecture,
			String city, String street, String groupnum) {
		Key key = KeyFactory.createKey(Rinban.class.getSimpleName(), prefecture
				+ city + street);
		Rinban entry = new Rinban(key, prefecture, city, street, groupnum);

		Boolean uniq = false;
		try {
			Rinban checkdup = pm.getObjectById(Rinban.class, key);
			entry = new Rinban(key, prefecture, city, street, groupnum + ","
					+ checkdup.getgroupnum());
			uniq = true;
		} catch (JDOObjectNotFoundException e) {
			uniq = true;
		}
		if (uniq) {
			pm.makePersistent(entry);
		}
	}
}

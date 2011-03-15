package jp.gr.jin.ocao;

import java.io.IOException;
import javax.servlet.http.*;
import java.lang.String;
import javax.servlet.*;
import java.util.Collections;
import javax.cache.Cache;
import javax.cache.CacheFactory;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.jdo.PersistenceManager;

import jp.gr.jin.ocao.Rinban;
import jp.gr.jin.ocao.PMF;

@SuppressWarnings("serial")
public class RinbanServlet extends HttpServlet {
	boolean cflag = true;
	Cache cache;

	public void init() throws ServletException {
		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
		} catch (CacheException e) {
			cflag = false;
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		if (!cflag) {
			return;
		}
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");

		String key = req.getParameter("key");
		String value_s = req.getParameter("value");
		byte[] value = value_s.getBytes();
		cache.put(key, value);
		resp.getWriter().println(
				"キー=" + key + "  バリュー=" + value_s + " が登録されました");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		if (!cflag) {
			return;
		}
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");
		String prefecture = req.getParameter("prefecture");
		String city = req.getParameter("city");
		String street = req.getParameter("street");
		String key = prefecture + city + street;
		byte[] value = (byte[]) cache.get(key);
		if (value != null) {
			String value_s = new String(value, "utf-8");
			resp.getWriter().println(
					"検索結果： 市町村名=" + key + "  グループ番号=" + value_s);
		} else {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				Rinban cachemiss = pm.getObjectById(Rinban.class, key);
				String groupnumber = cachemiss.getgroupnum();
				byte[] value_set = (byte[])groupnumber.getBytes();
				cache.put(key, value_set);
				resp.getWriter().println(
						"検索結果： 市町村名=" + key + "  グループ番号=" + groupnumber);

			} finally {
				pm.close();
			}
		}
	}
}

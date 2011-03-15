package jp.gr.jin.ocao;


import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.datastore.Key;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Rinban {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String prefecture;
    
    @Persistent
    private String city;
    
    @Persistent
    private String street;

    @Persistent
    private String groupnum;
    

    public Rinban(Key key, String prefecture, String city, String street, String groupnum) {
        this.prefecture  = prefecture;
        this.city = city;
        this.street = street;
        this.groupnum = groupnum;
        this.key = key;
    }
    
    public String getgroupnum() {
    	return groupnum;
    }
    
    public void doregist() {
    	PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(this);
        } finally {
            pm.close();
        }
    }
}
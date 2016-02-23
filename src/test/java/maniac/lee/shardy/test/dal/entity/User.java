package maniac.lee.shardy.test.dal.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by peng on 15/12/23.
 */
public class User implements Serializable{

    private static final long serialVersionUID = 5799155563595726310L;
    long id;
    String name;
    Date gmtCreate;
    Integer sex;
    String email;
    String imageThumbUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageThumbUrl() {
        return imageThumbUrl;
    }

    public void setImageThumbUrl(String imageThumbUrl) {
        this.imageThumbUrl = imageThumbUrl;
    }
}

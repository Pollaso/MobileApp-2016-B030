package ipn.mobileapp.model.dao.user;

import java.util.List;

import ipn.mobileapp.model.pojo.User;

public interface IUserDao {
    public User findById(String _id);
    public List<User> findByUserId(String userId);
    public boolean insert(User user);
    public boolean insert(List<User> users);
    public boolean update(User user);
    public boolean delete();
    public boolean delete(String _id);
}

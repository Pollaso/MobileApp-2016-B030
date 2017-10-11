package ipn.mobileapp.model.service.dao.user;

import java.util.List;

import ipn.mobileapp.model.pojo.User;

public interface IUserDao {
    User findById(String _id);
    List<User> findByUserId(String userId);
    boolean insert(User user);
    boolean insert(List<User> users);
    boolean update(User user);
    boolean delete();
    boolean delete(String _id);
}

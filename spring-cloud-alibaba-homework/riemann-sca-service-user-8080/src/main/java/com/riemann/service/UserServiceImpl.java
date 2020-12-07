package com.riemann.service;

import com.riemann.dao.TokenDao;
import com.riemann.dao.UserDao;
import com.riemann.pojo.Token;
import com.riemann.pojo.User;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TokenDao tokenDao;

    @Override
    public String register(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        Example<? extends User> userExample = Example.of(user);
        if (userDao.exists(userExample)) {
            return "";
        } else {
            userDao.save(user);
            Token token = new Token();
            token.setEmail(email);
            String uuid = UUID.randomUUID().toString();
            token.setToken(uuid);
            tokenDao.save(token);
            return uuid;
        }
    }

    @Override
    public boolean isRegistered(String email) {
        User user = new User();
        user.setEmail(email);
        Example<? extends User> userExample = Example.of(user);
        if (userDao.exists(userExample)) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public String login(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        Example<? extends User> userExample = Example.of(user);
        Optional<? extends User> one = userDao.findOne(userExample);
        if (!one.isPresent()) {
            return "";
        } else {
            Token token = new Token();
            token.setEmail(email);
            String uuid = UUID.randomUUID().toString();
            token.setToken(uuid);
            tokenDao.save(token);
            return uuid;
        }
    }

    @Override
    public String info(String token) {
        Token tmpToken = new Token();
        tmpToken.setToken(token);
        Example<? extends Token> tokenExample = Example.of(tmpToken);
        Optional<? extends Token> optionalToken = tokenDao.findOne(tokenExample);
        if (!optionalToken.isPresent()) {
            return "";
        }
        Token result = optionalToken.get();
        return result.getEmail();
    }

}

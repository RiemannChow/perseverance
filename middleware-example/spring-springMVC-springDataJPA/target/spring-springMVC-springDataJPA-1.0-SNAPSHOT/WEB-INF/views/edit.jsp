<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.riemann.pojo.Resume" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>编辑简历</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>

<div class="container" style="width: 500px">
    <h2>编辑简历</h2>
    <form action="/edit" method="post" role="form" class="form-horizontal addUser_tab">
        <input type="hidden" name="id" value="${resume.id}"/>
        <div class="form-group">
            <label class="col-sm-2 control-label">姓名</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" name="name" placeholder="请输入名字" value="${resume.name}">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label">手机号</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" name="phone" placeholder="手机号" value="${resume.phone}">
            </div>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label">地址</label>
            <div class="col-sm-10">
                <input type="text" class="form-control" name="address" placeholder="地址" value="${resume.address}">
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-7">
                <button type="submit" class="btn btn-default">修改</button>
                <button type="button" class="btn btn-default" type="button" id="btn_backward_page">返回</button>
            </div>
        </div>
    </form>
</div>
</body>
<script type="text/javascript">
    $('#btn_backward_page').on("click", function () {
        location.href = "localhost:8888/queryAll";
    });
</script>
</html>
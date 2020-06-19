<%@ page import="java.util.ArrayList" %>
<%@ page import="com.riemann.pojo.Resume" %>
<%@ page language="java" isELIgnored="false" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html >
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>简历列表</title>
    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>

</head>
<body>
<div class="container">
    <h2>简历列表</h2>


    <div>
        <button type="button" class="btn btn-default" id="add_rows" style="float: right; margin-bottom: 10px;">增加
        </button>
    </div>


    <table class="table table-bordered">
        <thead>
            <tr>
                <th>Id</th>
                <th>姓名</th>
                <th>手机号</th>
                <th>地址</th>
                <th>操作</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${resumeList}" var="resume">
                <tr>
                    <td>${resume.id}</td>
                    <td>${resume.name}</td>
                    <td>${resume.phone}</td>
                    <td>${resume.address}</td>
                    <td>
                        <button type="button" class="btn btn-default" onclick="modifySingleUser(${resume.id})">编辑</button>
                        <button type="button" class="btn btn-default" onclick="confirmDeleteUser(${resume.id})">删除</button>
                    </td>
                </tr>
            </c:forEach>

        </tbody>
    </table>

</div>
<script type="text/javascript">

    var flag = true;

    /* 是否删除单行 */
    function confirmDeleteUser(id) {
        if (confirm("请确认是否删除?")) {
            location.href = "localhost:8888/deleteById?id=" + id;
        } else {

        }
    }

    /* 是否修改单行 */
    function modifySingleUser(id) {
        location.href = "localhost:8888/queryById?id=" + id;
    }


    /* 增加成员 */
    $("#add_rows").click(function () {
        location.href = "localhost:8888/add";
    });

</script>
</body>

</html>
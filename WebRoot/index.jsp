<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>Device Monitor</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<link href="http://libs.baidu.com/bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet">
<script src="http://libs.baidu.com/jquery/2.0.0/jquery.min.js"></script>
<script src="http://libs.baidu.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<script type="text/javascript" src="./js/jquery-1.7.2.js"></script>
<script type="text/javascript">
	$(function() {
		setInterval(getScreenShot, 1000);
	});

	function getScreenShot() {
		$.getJSON("./GetScreenShot", "null", function(data) {
			for (var i = 0; i < data.length; i++) {
				/*$("#devices").append("<img src='"+convertURL('http://localhost:8080/AndroidMontior/4df1c0803b2d6f4d.png')+"' width='240' height='360' />");
				$("#devices").fadeIn("slow");*/

				$("#image" + (i + 1)).attr("src",
						convertURL("./" + data[i].serialNumber + ".jpg"));
				$("#image" + (i + 1)).attr("style", "");

			}
		});
	}

	/**
	 *给url加上时间戳
	 */
	function convertURL(url) {
		var timestamp = (new Date()).valueOf();
		if (url.indexOf("?") >= 0) {
			url = url + "&t=" + timestamp;
		} else {
			url = url + "?t=" + timestamp;
		}
		return url;
	}
</script>
</head>

<body>
	<div class="page-header">
  		<h1>远程设备监控<small></small></h1>
	</div>
	
	<div id="devices">
		<c:set var="currentIndex" />
		<c:if test="${!empty deviceInfos}">

			<c:forEach items="${deviceInfos }" var="deviceInfo"
				varStatus="status">
				<table class="table table-striped" style="width:300px;float:left;text-align:center;font-size:12px;">
					<tr>
						<td colspan="2" style="font-size:20px;font-weight:bold;">${deviceInfo.model }</td>
					</tr>
					<tr>
						<td height="246" colspan="2"><div align="center">
								<img id="image${status.index+1 }" src="" width='240'
									height='320' />
							</div></td>
					</tr>
					<tr>
						<td>品牌:</td>
						<td>${deviceInfo.manufacturer}</td>
					</tr>
					<tr>
						<td>分辨率:</td>
						<td>${deviceInfo.width }x${deviceInfo.height}</td>
					</tr>
					
					<tr>
						<td>版本:</td>
						<td>${deviceInfo.version }</td>
					</tr>
					<tr>
						<td>串号:</td>
						<td>${deviceInfo.serialNumber }</td>
					</tr>
					<tr>
					    <td colspan="2" rowspan="2">
					        <a href="./device?serialNumber=${deviceInfo.serialNumber}" class="btn btn-primary btn-lg" role="button">进入调试</a>
					    </td>
                    </tr>
				</table>
				
			</c:forEach>
		</c:if>
		<div style="clear:both;"></div>
	</div>


</body>
</html>

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	/* String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/"; */
	String basePath = request.getScheme() + "://" + request.getServerName() + path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'device.jsp' starting page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<script type="text/javascript" src="./js/jquery-1.7.2.js"></script>
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

</head>

<body>
	<p>此页面需要用支持HTML5的浏览器打开</p>
	<table border="0" cellpadding="0">
		<tr>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td rowspan="6" align="center"><canvas id="myCanvas" width="240"
					height="320" style="border:1px solid #d3d3d3;"> 您的浏览器不支持
				HTML5 canvas 标签。</canvas></td>
			<td>品牌:</td>
			<td>${deviceInfo.manufacturer}</td>
		</tr>
		<tr>
			<td>分辨率:</td>
			<td>${deviceInfo.width }x${deviceInfo.height}</td>
		</tr>
		<tr>
			<td>型号:</td>
			<td>${deviceInfo.model }</td>
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
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td height="45" align="center"><button
					onclick="SendKeyEvent('${deviceInfo.serialNumber }','82');">菜单</button>
				<button onclick="SendKeyEvent('${deviceInfo.serialNumber }','3');">首页</button>
				<button onclick="SendKeyEvent('${deviceInfo.serialNumber }','4');">返回</button></td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
	</table>
	<script>
		var c = document.getElementById("myCanvas");
		var ctx = c.getContext("2d");
		var img = new Image();
		var started = false;
		var targetX;
		var targetY;
		var Event_DownX;
		var Event_DownY;
		img.src = "./${serialNumber}.jpg?t=1453110102683";
		img.onload = function() {
			ctx.drawImage(img, 0, 0);
			c.addEventListener("mousedown", keyDown, false);
			c.addEventListener("mouseup", keyUp, false);
			c.addEventListener("keyMove", keyMove, false);
		}

		$(function() {
			setInterval(getScreenShot, 500);
		});

		function getScreenShot() {
			$.getJSON("./GetScreenShot", "null", function(data) {
				img.src = convertURL("./${serialNumber}.jpg");
				ctx.drawImage(img, 0, 0);
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

		function keyUp(evt) {
			Event_UpX = evt.offsetX;
			Event_UpY = evt.offsetY;
			//alert(Event_UpX+","+Event_UpY);
			//drawImage();
			//started=false;
			started = true;
			if (Event_DownX == Event_UpX && Event_DownY == Event_UpY) {
				//alert("只走点击事件");
				setKeyEvent("${serialNumber}", Event_DownX, Event_DownY);
			} else {
				keyMove(evt);
			}
		}

		function keyMove(evt) {
			Event_UpX = evt.offsetX;
			Event_UpY = evt.offsetY;
			//alert(Event_UpX+","+Event_UpY);
			//线条的颜色 
			ctx.strokeStyle = "#FF0000";
			//线条的宽度像素 
			ctx.lineWidth = 15;
			//线条的两关形状 
			ctx.lineCap = "round";
			ctx.lineTo(Event_UpX, Event_UpY);
			ctx.stroke();
			SendSwipeEvent("${serialNumber}", Event_DownX, Event_DownY,
					Event_UpX, Event_UpY);
		}

		function keyDown(evt) {
			Event_DownX = evt.offsetX;
			Event_DownY = evt.offsetY;
			//alert(Event_UpX+","+Event_UpY);
			drawString(Event_DownX, Event_DownY);
			ctx.beginPath();
			ctx.moveTo(Event_DownX, Event_DownY);
			started = false;
		}

		function drawString(x, y) {
			var c = document.getElementById("myCanvas");
			var cxt = c.getContext("2d");
			cxt.fillStyle = "#FF0000";
			cxt.beginPath();
			cxt.arc(x, y, 8, 0, Math.PI * 2, true);
			cxt.closePath();
			cxt.fill();
		}

		function drawImage() {
			//alert("释放了")
			var c = document.getElementById("myCanvas");
			var cxt = c.getContext("2d");
			img.src = "./${serialNumber}.jpg?t=1453110102683";
			ctx.drawImage(img, 0, 0);
		}

		function getPointOnCanvas(canvas, x, y) {
			var bbox = canvas.getBoundingClientRect();
			return {
				x : x - bbox.left * (canvas.width / bbox.width),
				y : y - bbox.top * (canvas.height / bbox.height)
			};
		}

		function setKeyEvent(serialNumber, locationX, locationY) {
			$.get("./SendTapEvent", {
				serialNumber : serialNumber,
				locationX : locationX,
				locationY : locationY
			}, function(data) {

			});
		}

		function SendSwipeEvent(serialNumber, locationX, locationY, targetX,
				targetY) {
			$.get("./SendSwipeEvent", {
				serialNumber : serialNumber,
				locationX : locationX,
				locationY : locationY,
				targetX : targetX,
				targetY : targetY
			}, function(data) {

			});
		}

		function SendKeyEvent(serialNumber, keyCode) {
			$.get("./SendKeyEvent", {
				serialNumber : serialNumber,
				keyCode : keyCode
			}, function(data) {

			});
		}
	</script>
</body>
</html>

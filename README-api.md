接口一：https://application.xiaofubao.com/app/login/getUser4Authorize

目的：用于获取payOpenid和thirdOpenid

请求参数：

userId：必须

schoolCode：16301

platform：WECHAT_H5

ymId：非必须

接口二：https://application.xiaofubao.com/app/electric/queryBind

目的：用于返回id和userId以及住宿信息

请求参数：

bindType：1

platform：WECHAT_H5

ymId：非必须

接口三： https://application.xiaofubao.com/app/electric/recharge

目的：用于获取订单号

以19-401为例

请求参数：

areaId：2307499265384382465

buildingCode：009

floorCode：009002

roomCode：19-401

money，submitToken，platform，extJson，ymlId
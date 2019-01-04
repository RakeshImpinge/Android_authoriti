
//
//var json_obj = {
//    "accounts": [{
//            "confirmed": true,
//            "value": "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3",
//            "type": "123"
//        }],
//    "encryptKey": "LspSbOsdao2Xx3vk0GcNdQ==:CIIXpKs5M9ZCWY/9ZZpT+a8sQt+kUvhIzkY0ayWSjSY=",
//    "encryptPassword": "a+IJwYkhpbqIWMEiUm39og==:yX5wpfLNkYEfV5WZHcJSd3mOUINAWjdzpEC0PX+vv5k=:p9yKqvRAUwqWC/jNaF1P5A==",
//    "encryptPrivateKey": "9t+aVC3kZRr8RJH1rY8exw==:9s4z6/o2e1xglB7mMliLQhPPEI6DQiwv15+OlvpqIoc=:I+1xqssViXcPCYO7AwB4NA==",
//    "encryptSalt": "yVPkKGAVg8fii5BZzmpQ+A==:UXA7ZCsRO+Mv2um91rz52p7WwxSVRZ6eHk68TWnLmFE=:9i1RxBNdegE+qFfXGFWnVw==",
//    "fingerPrintAuthEnabled": false,
//    "invite_code": "Startup2018",
//    "isChaseType": false,
//    "password": "123456",
//    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxNWU4ZDkyYi02MTFiLTQ2YTgtYWQ2ZS04ZTAyOTIzMjJkNTEiLCJpYXQiOjE1MzczNTE1ODh9.TdJ3TpAnqVz0jv6AEKDi2jVQEivZ8lc-2_lTgI5cca8",
//    "id": "15e8d92b-611b-46a8-ad6e-8e0292322d51"
//}


var json_schema = SObject([
    ["accounts", SList(
        SObject([
            ["Customer", SString()],
            ["Customer_ID", SString()],
            ["confirmed", SBoolean()],
            ["hashed", SBoolean()],
            ["value", SHex(32, "lower")],
            ["type", SString()]
        ])
    )],
    ["encryptKey", SB64Tuple([16, 32], ":", true)],
    ["encryptPassword", SB64Tuple([16, 32, 16], ":", true)],
    ["encryptPrivateKey", SB64Tuple([16, 32, 16], ":", true)],
    ["encryptSalt", SB64Tuple([16, 32, 16], ":", true)],
    ["fingerPrintAuthEnabled", SBoolean()],
    ["invite_code", SString()],
    ["isChaseType", SBoolean()],
    ["password", SString()],
    ["token", SJWToken(
        {"alg": "HS256", "typ": "JWT"},
        SJWTPayload([
            ["userId", SHexTuple([4, 2, 2, 2, 6], "lower", "-")],
            ["iat", SUint32()]
        ]),
        [["userId", "id"]]
    )]
    // id is taken from the token
]);



////var json_obj = JSON.parse(json_string);
//var compacted_raw = json_compactor.compact(json_obj, json_schema, true);
//var compacted_b85 = json_compactor.compact(json_obj, json_schema);
//var decompacted   = json_compactor.decompact(compacted_b85, json_schema);
////var decompacted   = json_compactor.decompact(compacted_raw, json_schema, true);
//
//
//const entityMap = {
//  '&': '&amp;',
//  '<': '&lt;',
//  '>': '&gt;',
//  '"': '&quot;',
//  "'": '&#39;',
//  '/': '&#x2F;',
//  '`': '&#x60;',
//  '=': '&#x3D;'
//};
//
//function escapeHtml (string) {
//  return String(string).replace(/[&<>"'`=\/]/g, function (s) {
//    return entityMap[s];
//  });
//}
//
//
//document.getElementById("input").innerHTML = JSON.stringify(json_obj, null, 4);
//document.getElementById("json-size").innerHTML = (JSON.stringify(json_obj)).length;
//
//document.getElementById("compacted-b85").innerHTML = escapeHtml(compacted_b85);
//document.getElementById("compacted-b85-size").innerHTML = compacted_b85.length;
//
//document.getElementById("compacted-raw-size").innerHTML = compacted_raw.length;
//
//document.getElementById("decompacted").innerHTML = JSON.stringify(decompacted, null, 4);
//
//
//// QR Code (Base85)
//var qrcode = new QRCode(
//    document.getElementById("qrcode-b85"), {
//        text: compacted_b85,
//        correctLevel: QRCode.CorrectLevel.L
//});
//
//// QR Code (raw binary)
//var qrcode = new QRCode(
//    document.getElementById("qrcode-raw"), {
//        text: compacted_raw,
//        correctLevel: QRCode.CorrectLevel.L
//});

//var url_string = window.location.href
//var url = new URL(url_string);
//
//var type= url.searchParams.get("type");
//var data= url.searchParams.get("data");
//
//console.log("Here is the type=>"+type);
//console.log("Here is the data=>"+data);
//
//if(type.localeCompare("encode")==0){
//    var json= JSON.parse(url.searchParams.get("data"));
//    Android.Data(json_compactor.compact(json, json_schema));
//}else{
//    var encodedString= url.searchParams.get("data");
//    Android.Data(JSON.stringify(json_compactor.decompact(encodedString, json_schema), null, 4));
//}
//


function getJsonData(encode_data)
{
    var decodedString = window.atob(encode_data);
    console.log("getJsonData=>"+decodedString);
    Android.Data(JSON.stringify(json_compactor.decompact(decodedString, json_schema), null, 4));
}

function getEncodedData(json_data)
{
    console.log("getEncodedData=>"+json_data);
    var json= JSON.parse(json_data);
    Android.Data(json_compactor.compact(json, json_schema));
}







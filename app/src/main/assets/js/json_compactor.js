

String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
}

String.prototype.repeat = function(num) {
    return new Array(num + 1).join(this);
}


function hexToBytes(hex) {
    for (var bytes = [], c = 0; c < hex.length; c += 2)
    bytes.push(parseInt(hex.substr(c, 2), 16));
    return bytes;
}

function bytesToHex(bytes) {
    for (var hex = [], i = 0; i < bytes.length; i++) {
        hex.push((bytes[i] >>> 4).toString(16));
        hex.push((bytes[i] & 0xF).toString(16));
    }
    return hex.join("");
}

function typedArrayToArray(typedArray) {
    return [].slice.call(typedArray);
}

function binaryToString(binary) {
    return String.fromCharCode.apply(null, binary);
}

function stringToBinary(string) {
    var output = new Array(string.length);
    for (var i = 0; i < string.length; i++) {
        output[i] = string.charCodeAt(i);
    }
    return output;
}

function b64repad(b64) {
    return b64 + "=".repeat((4 - (b64.length % 4)) % 4);
}

function b64unpad(b64) {
    return b64.replace(/=+$/, "");
}


function SObject(kvs) {
    return {
        "serialize": function(json) {
            var output = [];
            for (var i in kvs) {
                var name = kvs[i][0];
                var schema = kvs[i][1];
                console.log("Name", name);
                console.log("Schema", schema);
                output = output.concat(schema.serialize(json[name]));
            }
            return output;
        },

        "deserialize": function(serialized) {
            var output = {};
            for (var i in kvs) {
                var name = kvs[i][0];
                console.log(name);
                var schema = kvs[i][1];

                if (schema.extract == undefined) {
                    output[name] = schema.deserialize(serialized);
                } else {
                    var _kvs = schema.extract(serialized);
                    output[name] = schema.deserialize(serialized);
                    for (var j in _kvs) {
                        output[_kvs[j][0]] = _kvs[j][1];
                    }
                }
            }
            return output;
        }
    }
}


function SList(element_schema) {
    return {
        "serialize": function(json) {
            var output = [];
            for (var i in json) {
                var element = json[i];
                output = output.concat(element_schema.serialize(element), [255]);
            }
            output[output.length - 1] = 0;
            return output;
        },

        "deserialize": function(serialized) {
            var output = [];
            while (true) {
                output.push(element_schema.deserialize(serialized));
                if (serialized[0] === 0) {
                    serialized.shift();
                    break;
                }
                serialized.shift();
            }
            return output;
        }
    }
}


function SUint32() {
    return {
        "serialize": function(value) {
            return [
                (value >> 24) % 256,
                (value >> 16) % 256,
                (value >>  8) % 256,
                (value      ) % 256
            ];
        },

        "deserialize": function(serialized) {
            var bytes = serialized.splice(0, 4);
            return (bytes[0] << 24) |
                   (bytes[1] << 16) |
                   (bytes[2] <<  8) |
                   (bytes[3]      );
        }
    }
}


function SBoolean() {
    return {
        "serialize": function(value) {
            return value ? [1] : [0];
        },

        "deserialize": function(serialized) {
            var byte = serialized.shift();
            return byte == 0 ? false : true;
        }
    }
}


function SHex(size, case_) {
    if (case_ == undefined) {
        case_ = "lower";
    }
    return {
        "serialize": function(value) {
            return hexToBytes(value);
        },

        "deserialize": function(serialized) {
            var hex = bytesToHex(serialized.splice(0, size));
            if (case_ === "lower") {
                return hex.toLowerCase();
            } else if (case_ === "upper") {
                return hex.toUpperCase();
            }
        }
    }
}


function SHexTuple(sizes, case_, joint) {
    var hexes = [];
    for (var i in sizes) {
        hexes.push(SHex(sizes[i], case_));
    }
    return {
        "serialize": function(value) {
            return hexToBytes(value.replaceAll(joint, ""));
        },

        "deserialize": function(serialized) {
            segments = [];
            for (var i in hexes) {
                segments.push(hexes[i].deserialize(serialized));
            }
            return segments.join(joint);
        }
    }
}


function SString() {
    return {
        "serialize": function(value) {
            return typedArrayToArray(new TextEncoder("utf-8").encode(value))
                   .concat([0]);
        },

        "deserialize": function(serialized) {
            var n = 0;
            while (serialized[n] !== 0) {
                n += 1;
            }
            var bytes = serialized.splice(0, n + 1).slice(0, n);
            return new TextDecoder("utf-8").decode(new Uint8Array(bytes));
        }
    }
}


function SB64(size, padded) {
    if (typeof padded == undefined) {
        padded = true;
    }
    return {
        "serialize": function(value) {
            var binary_string = atob(value);
            return stringToBinary(binary_string);
        },

        "deserialize": function(serialized) {
            var binary = serialized.splice(0, size);
            var as_string = binaryToString(binary);
            return btoa(as_string);
        }
    }
}


function SB64Tuple(sizes, joint, padded) {
    var b64s = [];
    for (var i in sizes) {
        b64s.push(SB64(sizes[i], padded));
    }
    return {
        "serialize": function(value) {
            var output = [];
            var split = value.split(joint);
            for (var i in split) {
                output = output.concat(b64s[i].serialize(split[i]));
            }
            return output;
        },

        "deserialize": function(serialized) {
            var output = [];
            for (var i in b64s) {
                var b64 = b64s[i].deserialize(serialized);
                if (!padded) {
                    b64 = b64unpad(b64);
                }
                output.push(b64);
            }
            console.log(output.join(joint));
            return output.join(joint);
        }
    }
}


function SJWTPayload(kvs) {
    return {
        "serialize": function(json) {
            var output = [];
            for (var i in kvs) {
                var name = kvs[i][0];
                var schema = kvs[i][1];
                output = output.concat(schema.serialize(json[name]));
            }
            return output;
        },

        "deserialize": function(serialized) {
            output = {};
            for (var i in kvs) {
                var name = kvs[i][0];
                var schema = kvs[i][1];
                output[name] = schema.deserialize(serialized);
            }
            return output;
        }
    }
}


function b64decode_urlsafe(b64) {
    var b64 = b64.replaceAll("-", "+").replaceAll("_", "/");
    return atob(b64);
}

function b64encode_urlsafe(data) {
    var b64 = btoa(data);
    return b64.replaceAll("+", "-").replaceAll("/", "_");
}

function SJWToken(header, payload_schema, payload_extractions) {
    var signature_size = Number(header["alg"].slice(2)) / 8;
    return {
        "serialize": function(value) {
            var split = value.split(".");
            payload = JSON.parse(b64decode_urlsafe(split[1]));
            signature = stringToBinary(b64decode_urlsafe(split[2]));

            var output = payload_schema.serialize(payload).concat(signature);
            return output;
        },


        "deserialize": function(serialized) {
            var payload = payload_schema.deserialize(serialized);
            var signature = binaryToString(serialized.splice(0, signature_size));

            var segments = [
                b64unpad(b64encode_urlsafe(JSON.stringify(header))),
                b64unpad(b64encode_urlsafe(JSON.stringify(payload))),
                b64unpad(b64encode_urlsafe(signature)),
            ];
            return segments.join(".");
        },

        "extract": function(serialized) {
            var copy = serialized.slice();
            var payload = payload_schema.deserialize(copy);
            output = [];
            for (var i in payload_extractions) {
                var name_payload = payload_extractions[i][0];
                var name_output = payload_extractions[i][1];
                output.push([name_output, payload[name_payload]]);
            }
            return output;
        }
    }
}


// https://stackoverflow.com/a/29415858
// By Steve Hanov. Released to the public domain.
function encode_ascii85(input) {
  var output = "<~";
  var chr1, chr2, chr3, chr4, chr, enc1, enc2, enc3, enc4, enc5;
  var i = 0;

  while (i < input.length) {
    // Access past the end of the string is intentional.
    chr1 = input.charCodeAt(i++);
    chr2 = input.charCodeAt(i++);
    chr3 = input.charCodeAt(i++);
    chr4 = input.charCodeAt(i++);

    chr = ((chr1 << 24) | (chr2 << 16) | (chr3 << 8) | chr4) >>> 0;

    enc1 = (chr / (85 * 85 * 85 * 85) | 0) % 85 + 33;
    enc2 = (chr / (85 * 85 * 85) | 0) % 85 + 33;
    enc3 = (chr / (85 * 85) | 0 ) % 85 + 33;
    enc4 = (chr / 85 | 0) % 85 + 33;
    enc5 = chr % 85 + 33;

    output += String.fromCharCode(enc1) +
      String.fromCharCode(enc2);
    if (!isNaN(chr2)) {
      output += String.fromCharCode(enc3);
      if (!isNaN(chr3)) {
        output += String.fromCharCode(enc4);
        if (!isNaN(chr4)) {
          output += String.fromCharCode(enc5);
        }
      }
    }
  }

  output += "~>";

  return output;
}

// https://stackoverflow.com/a/31741264
function decode_ascii85(a) {
  var c, d, e, f, g, h = String, l = "length", w = 255, x = "charCodeAt", y = "slice", z = "replace";
  for ("<~" === a[y](0, 2) && "~>" === a[y](-2), a = a[y](2, -2)[z](/\s/g, "")[z]("z", "!!!!!"), 
  c = "uuuuu"[y](a[l] % 5 || 5), a += c, e = [], f = 0, g = a[l]; g > f; f += 5) d = 52200625 * (a[x](f) - 33) + 614125 * (a[x](f + 1) - 33) + 7225 * (a[x](f + 2) - 33) + 85 * (a[x](f + 3) - 33) + (a[x](f + 4) - 33), 
  e.push(w & d >> 24, w & d >> 16, w & d >> 8, w & d);
  return function(a, b) {
    for (var c = b; c > 0; c--) a.pop();
  }(e, c[l]), h.fromCharCode.apply(h, e);
}

var json_compactor = (function() {
    return {
        "compact": function(json_obj, schema, raw_binary) {
            if (raw_binary === undefined) {
                raw_binary = false;
            }
            var compacted = new Uint8Array(schema.serialize(json_obj));
            if (raw_binary) {
                return binaryToString(compacted);
            } else {
                return encode_ascii85(binaryToString(compacted)).slice(2, -2);
            }
        },

        "decompact": function(compacted, schema, raw_binary) {
            if (raw_binary === undefined) {
                raw_binary = false;
            }
            if (!raw_binary) {
                compacted = decode_ascii85("<~" + compacted + "~>");
            }
            var compacted = stringToBinary(compacted);
            return schema.deserialize(compacted);
        }
    };
}());

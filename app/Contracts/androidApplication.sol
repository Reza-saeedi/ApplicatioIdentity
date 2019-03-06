pragma solidity >=0.4.22 <0.6.0;

contract AndroidApplication {

  
  
    struct Application {
        string packageName;
        string name;
        string signature;
        address creator;
        string webSite;
    }
    
     address chairperson;
     
     mapping(string => Application[]) verified;
     mapping(string => Application[]) Verifier;
     mapping(address => Application[]) public ApplicationDatabase;
     mapping(string => Application) applications;
     

    event ApplicationAdded( string  app_packageName,
        string   app_name,
        string  app_publicKey);
        
    event ApplicationVerified( string  app_packageName,
        string  app_name,
        string  app_publicKey,
        address owner);
    
     
    function addApplication( 
        string memory  app_packageName,
        string memory  app_name,
        string memory  app_publicKey,
        string memory  app_N_of_publicKey,
        string memory app_webSite,
        string memory  app_challeng)public returns (bool success) {
             
             	require(
             	    
             	    RSAVerify(bytes(strConcat(app_packageName,app_name,app_publicKey)),bytes(app_challeng),bytes(app_publicKey),bytes(app_N_of_publicKey))==0
             	    
             	    );
             	
             Application memory  application=Application(
                 {packageName:app_packageName,
                 name :app_name,
                signature:app_publicKey,
                creator:msg.sender,
                webSite: app_webSite});
             
                Application memory app = applications[app_packageName];
                app.name=app_name;
                 app.packageName=app_packageName;
                 app.signature=app_publicKey;
                 app.creator=msg.sender;
                 app.webSite=app_webSite;
                 
                 ApplicationDatabase[msg.sender].push(application);
                 
            emit ApplicationAdded(app_packageName,app_name,app_publicKey);
            
            return true;
    }


    constructor() public {
        chairperson = msg.sender;
    }
    
    function verifyApplication(string memory  app_packageName,
        string memory  app_name,
        string memory  app_publicKey,uint myAppId) public  returns (bool) {
          Application memory  application = applications[app_packageName];
         
          verified[strConcat(app_packageName,app_name,app_publicKey)].push(ApplicationDatabase[msg.sender][myAppId]);
          Verifier[ApplicationDatabase[msg.sender][myAppId].packageName].push(application);
          
          emit ApplicationVerified(app_packageName,app_name,app_publicKey,msg.sender);
          
          return true;
    }
    
    function getVerifiedApp(string memory  app_packageName,
        string memory  app_name,
        string memory  app_signature) public view returns (string memory) {
          
          return verified[strConcat(app_packageName,app_name,app_signature)][0].packageName;
        
    }
    
    function getVerifierApp(uint myAppId) public view returns (string memory) {
         return Verifier[ApplicationDatabase[msg.sender][myAppId].packageName][0].packageName;
    }
    
     function isAppVerifyBydeveloper(string memory  app_packageName,
        string memory  app_name,
        string memory  app_signature) public view returns (address) {
        
          return applications[strConcat(app_packageName,app_name,app_signature)].creator;
    }
    
  
    function strConcat(string memory _a, string memory _b, string memory  _c, string memory _d, string memory _e) internal pure returns (string memory){
    bytes memory _ba = bytes(_a);
    bytes memory _bb = bytes(_b);
    bytes memory _bc = bytes(_c);
    bytes memory _bd = bytes(_d);
    bytes memory _be = bytes(_e);
    string memory abcde = new string(_ba.length + _bb.length + _bc.length + _bd.length + _be.length);
    bytes memory babcde = bytes(abcde);
    uint k = 0;
    for (uint i = 0; i < _ba.length; i++) babcde[k++] = _ba[i];
    for (uint i = 0; i < _bb.length; i++) babcde[k++] = _bb[i];
    for (uint i = 0; i < _bc.length; i++) babcde[k++] = _bc[i];
    for (uint i = 0; i < _bd.length; i++) babcde[k++] = _bd[i];
    for (uint i = 0; i < _be.length; i++) babcde[k++] = _be[i];
    return string(babcde);
}

function strConcat(string memory _a, string memory _b, string memory _c, string memory _d) internal pure returns (string memory) {
    return strConcat(_a, _b, _c, _d, "");
}

function strConcat(string memory _a, string memory _b, string memory _c) internal pure returns (string memory) {
    return strConcat(_a, _b, _c, "", "");
}

function strConcat(string memory  _a, string memory _b) internal pure returns (string memory) {
    return strConcat(_a, _b, "", "", "");
}

     
     
       function memcpy(uint _dest, uint _src, uint _len) pure internal {
        // Copy word-length chunks while possible
        for ( ;_len >= 32; _len -= 32) {
            assembly {
                mstore(_dest, mload(_src))
            }
            _dest += 32;
            _src += 32;
        }

        // Copy remaining bytes
        uint mask = 256 ** (32 - _len) - 1;
        assembly {
            let srcpart := and(mload(_src), not(mask))
            let destpart := and(mload(_dest), mask)
            mstore(_dest, or(destpart, srcpart))
        }
    }

    
    function join(
	bytes memory _s, bytes memory _e, bytes memory _m
    ) pure internal returns (bytes memory) {
        uint inputLen = 0x60+_s.length+_e.length+_m.length;
        
        uint slen = _s.length;
        uint elen = _e.length;
        uint mlen = _m.length;
        uint sptr;
        uint eptr;
        uint mptr;
        uint inputPtr;
        
        bytes memory input = new bytes(inputLen);
        assembly {
            sptr := add(_s,0x20)
            eptr := add(_e,0x20)
            mptr := add(_m,0x20)
            mstore(add(input,0x20),slen)
            mstore(add(input,0x40),elen)
            mstore(add(input,0x60),mlen)
            inputPtr := add(input,0x20)
        }
        memcpy(inputPtr+0x60,sptr,_s.length);        
        memcpy(inputPtr+0x60+_s.length,eptr,_e.length);        
        memcpy(inputPtr+0x60+_s.length+_e.length,mptr,_m.length);

        return input;
    }
    
    /** @dev Verifies a PKCSv1.5 SHA256 signature
      * @param _sha256 is the sha256 of the data
      * @param _s is the signature
      * @param _e is the exponent
      * @param _m is the modulus
      * @return 0 if success, >0 otherwise
    */    
    function pkcs1Sha256Verify(
        bytes32 _sha256,
        bytes memory _s, bytes memory _e, bytes memory _m
    ) public view returns (uint) {
        
        uint8[19] memory sha256Prefix = [
            0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x01, 0x05, 0x00, 0x04, 0x20
        ];
        
      	require(_m.length >= sha256Prefix.length+_sha256.length+11);

        uint i;

        /// decipher
        bytes memory input = join(_s,_e,_m);
        uint inputlen = input.length;

        uint decipherlen = _m.length;
        bytes memory decipher = new bytes(decipherlen);
        assembly {
            pop(staticcall(sub(gas, 2000), 5, add(input,0x20), inputlen, add(decipher,0x20), decipherlen))
	}
        
        /// 0x00 || 0x01 || PS || 0x00 || DigestInfo
        /// PS is padding filled with 0xff
        //  DigestInfo ::= SEQUENCE {
        //     digestAlgorithm AlgorithmIdentifier,
        //     digest OCTET STRING
        //  }
        
        uint paddingLen = decipherlen - 3 - sha256Prefix.length - 32;
        
        if (decipher[0] != 0 || uint8(decipher[1]) != 1) {
            return 1;
        }
        for (i = 2;i<2+paddingLen;i++) {
            if (decipher[i] != 0xff) {
                return 2;
            }
        }
        if (decipher[2+paddingLen] != 0) {
            return 3;
        }
        for (i = 0;i<sha256Prefix.length;i++) {
            if (uint8(decipher[3+paddingLen+i])!=sha256Prefix[i]) {
                return 4;
            }
        }
        for (i = 0;i<_sha256.length;i++) {
            if (decipher[3+paddingLen+sha256Prefix.length+i]!=_sha256[i]) {
                return 5;
            }
        }

        return 0;
    }

    /** @dev Verifies a PKCSv1.5 SHA256 signature
      * @param _data to verify
      * @param _s is the signature
      * @param _e is the exponent
      * @param _m is the modulus
      * @return 0 if success, >0 otherwise
    */    
    function RSAVerify (
        bytes memory _data, 
        bytes memory _s, bytes memory _e, bytes memory _m
    ) public view returns (uint) {
        return pkcs1Sha256Verify(sha256(_data),_s,_e,_m);
    }

}



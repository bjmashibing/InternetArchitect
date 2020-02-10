pragma solidity ^0.5.12;

contract HelloWorld {
    
    // key,value 的 数据类型 
    mapping(address => uint) map;
    
    
    function getAge(address _addr)public view returns(uint age){
        
        return map[_addr];
        
    }
    
    
    function setAge(uint _age)public {
        
        map[msg.sender] = _age;
    }
    
    
    /*
    
    age 整形 256位   
    
    */
    // uint age;
    
    
    // function setAge(uint _age) public{
        
    //     age = _age;
    // }
    
    // function getAge()public view returns(uint){
        
    //     return age+1;
    // }
    
    
    function add(uint a, uint b)public pure returns(uint result,uint r_a){
        
        return (a + b,a);
    }
    
    function getUserAddr() public view returns(address addr){
        
       return msg.sender;
    }
    
    
    
    
}
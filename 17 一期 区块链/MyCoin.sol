pragma solidity ^0.5.12;


contract MyCoin{
    
    // ����Ա 
    address public operator;
    
    
    // ���е��˻���� 
    mapping(address => uint) balance;
    
    // ��̬����  
    constructor() public {
        
        operator = msg.sender;
        
    }
    
    // ����  0xdD870fA1b7C4700F2BD7f44238821C26f7392148
    function create(address recevier,uint amount) public{
        // ֻ���ɹ���Ա���� 
       require(msg.sender == operator);
        
        // ��һ���˻� �ӱ�
       balance[recevier] += amount;
        
    }
    
    // ת�� 0xdD870fA1b7C4700F2BD7f44238821C26f7392148  4,0xdD870fA1b7C4700F2BD7f44238821C26f7392148 1,0xCA35b7d915458EF540aDe6068dFe2F44E8fa733c
    
    function transfer(address recevier,uint amount)public {
        
        require(balance[msg.sender] >= amount);
        
        balance[msg.sender] -= amount;
        
        balance[recevier] += amount;
        
        
    }
    
    
    
    
    // �鿴��� 
    
    function viewBalance(address addr) public view returns(uint) {
        
        return balance[addr];
        
    }
    
}
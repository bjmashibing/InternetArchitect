/**
 * Created by yiming on 2017/4/28.
 */
	//对比图表
    var grid_data = [
        {
            invdate: "资产总额",
            name: "10W",
            amount: "2W",
        }, {
            invdate: "应收款项",
            name: "10W",
            amount: "2W",
        }, {

            invdate: "存货",
            name: "10W",
            amount: "2W",
        },{
            invdate: "其他流动资产",
            name: "10W",
            amount: "2W",
        }, {
  			invdate: "固定资产",
            name: "10W",
            amount: "2W",
        },{
  			invdate: "其他非流动资产",
            name: "10W",
            amount: "2W",
        }];

$(function(){
    pageInit();
});
function pageInit(){
    $("#list9").jqGrid({
        //编辑单元格 action
        //在 colModel 中还需要配置属性 editable : true
        cellEdit:true,
        cellsubmit:'clientArray',
        //编辑单元格 end
        data: grid_data, //当 datatype 为"local" 时需填写
        datatype: "local",
        styleUI:"Bootstrap",
        colNames : [ '', '年初', '期末'],
        colModel : [
            {name : 'invdate',index : 'invdate',width : 50,align:'center',editable : true},
            {name : 'name',index : 'name',width : 50,align:'center',editable : true},
            {name : 'amount',index : 'amount',width : 50,align:'center',sortable : false,editable : true},
        ],
        rowNum : 20,
        height:'auto',
        rowList : [ 20, 10, 20 ],//和rownum同步设置才能控制每页显示行数
        altRows:true,
        recordpos : 'left',
        loadonce: true,
        viewrecords : true,
        multiselect : false,//去掉复选框
        rownumbers : false,
        autowidth:true,
        gridComplete:function() {
            $("#list9").parents(".ui-jqgrid-bdiv").css("overflow-x", "hidden");
        },
        //cell保存回调事件
        afterSaveCell: function(data){
            compare_table();
        }
    });
}
//页面的父元素宽度变化时  table自适应宽度事件
$(".gdzcBoxL").resize(function(){
    $("#list9").setGridWidth($(".gdzcBoxL").width());
})
//对比当前数据和初始数据是否一致
function compare_table() {

    var data_current = JSON.stringify(grid_data);  //当前数据
    var data_init =  JSON.stringify(grid_data_init); //初始数据

    //数据是否改变
    var is_same = data_current == data_init ;
    if(is_same){
        //隐藏对比按钮
        layer.msg("已恢复初始值");
        show_compare_btn(false);
    }else{
        //显示对比按钮
        show_compare_btn(true);
        layer.msg("编辑成功");
    }
}

//是否显示对比按钮
function show_compare_btn(is_show){
    if(is_show){
        $("#compare_btn").show();
    }else{
        $("#compare_btn").hide();
    }
}

//弹出原始未修改表格
function show_init_data(){
    //填充初始数据
    init_table();
    layer.open({
        title: "初始数据对比" ,
        area: ['600px', '320px'],
        type: 1,
        shadeClose: true,
        maxmin: false,
        content: $('.data_compare_div'), //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
    	success: function(){
			$(".layui-layer-content").css({
				"padding":"10px",
				"overflow":"hidden",
			});
        }
    });
}
    //原始未修改数据            
    var grid_data_init = [
        {
            invdate: "资产总额",
            name: "10W",
            amount: "2W",
        }, {
            invdate: "应收款项",
            name: "10W",
            amount: "2W",
        }, {

            invdate: "存货",
            name: "10W",
            amount: "2W",
        },{
            invdate: "其他流动资产",
            name: "10W",
            amount: "2W",
        }, {
            invdate: "固定资产",
            name: "10W",
            amount: "2W",
        },{
            invdate: "其他非流动资产",
            name: "10W",
            amount: "2W",
        }];
//显示初始数据图表
function init_table(){
    $("#data_compare_list").jqGrid({
        data: grid_data_init, //当 datatype 为"local" 时需填写
        datatype: "local",
        styleUI:"Bootstrap",
        colNames : [ '', '年初', '期末'],
        colModel : [
            {name : 'invdate',index : 'invdate',width : 50,align:'center',editable : true},
            {name : 'name',index : 'name',width : 50,align:'center',editable : true},
            {name : 'amount',index : 'amount',width : 50,align:'center',sortable : false,editable : true},
        ],
        rowNum : 20,
        height:'auto',
        rowList : [ 20, 10, 20 ],//和rownum同步设置才能控制每页显示行数
        altRows:true,
        width:500,
        autowidth: false,
        shrinkToFit: true,
        recordpos : 'left',
        loadonce: true,
        viewrecords : true,
        multiselect : false,//去掉复选框
        rownumbers : false,
        gridComplete:function() {
            $("#data_compare_list").parents(".ui-jqgrid-bdiv").css("overflow-x", "hidden");
        },

    });
}
//页面的父元素宽度变化时  table自适应宽度事件
$(".data_compare_div").resize(function(){
    $("#data_compare_list").setGridWidth($(".data_compare_div").width());
})


//对比echarts图
//弹出初始数据
function show_init_chart_data(chart){
    //填充初始数据
    layer.open({
        title: "初始数据" ,
        area: ['500px', '350px'],
        type: 1,
        content: $('#data_compare_chart_div') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
    });
    init_chart_div(chart);
}

function  init_chart_div(chart) {
    var myChart = echarts.init(document.getElementById("data_compare_chart_list"),{
        noDataLoadingOption:{
            text: '暂无数据',
            effect: 'bubble',
            effectOption: {
                effect: {
                    n: 0
                }
            }
        }
    });

    var current_opt = chart.getOption();
    var  option = {
    	title : current_opt.title,
        tooltip :current_opt.tooltip,
        legend: current_opt.legend_bak,
        color:current_opt.color,
        calculable : current_opt.calculable,
        series : current_opt.series_bak ,
    };
    myChart.setOption(option);
    myChart.resize();
}


////资产情况echarts
//function zcqk(){
//  // 基于准备好的dom，初始化echarts实例
//  var myChart = echarts.init(document.getElementById("gdzcEcharts1"),{
//      noDataLoadingOption:{
//          text: '暂无数据',
//          effect: 'bubble',
//          effectOption: {
//              effect: {
//                  n: 0
//              }
//          }
//      }
//  });
//  option = {
//      toolbox: {
//          show : true,
//          feature : {
//              myTool : {
//                  show : true,
//                  title : '修改',
//                  icon : 'image://http://echarts.baidu.com/images/favicon.png',
//                  onclick : function (){
//                      modify_chart(myChart,'pie');
//                  }
//              },
//              myTool2 : {
//                  show : true,
//                  title : '对比',
//                  icon : 'image://http://echarts.baidu.com/images/favicon.png',
//                  onclick : function (){
//                      show_init_chart_data(myChart);
//                  }
//              },
//              saveAsImage : {show: true}
//          }
//      },
//
//
//      title : {
//          text: '年11初',
//          x: '28%',
//          y:'53%',
//          textStyle: {
//              color: '#000',// 图例文字颜色
//              fontSize:16,
//          }
//      },
//      tooltip : {
//          trigger: 'axis'
//      },
//      legend: {
//          orient: 'vertical',
//          x: '28%',
//          y:'62%',
//          itemWidth:10,
//          itemHeight:10,
//          data:['应收款项80','存货30','其他流动资产20','固定资产10','其他非流动资产2'],
//          itemGap:6,
//          textStyle: {
//              color: '#000',// 图例文字颜色
//              fontSize:13,
//          }
//      },
//      legend_bak: {
//          //备份数据用于数据对比
//          orient: 'vertical',
//          x: '28%',
//          y:'62%',
//          itemWidth:10,
//          itemHeight:10,
//          data:['应收款项80','存货30','其他流动资产20','固定资产10','其他非流动资产2'],
//          itemGap:6,
//          textStyle: {
//              color: '#000',// 图例文字颜色
//              fontSize:13,
//          }
//      },
//      //备份数据用于数据对比
//      legend_bak_data:['应收款项80','存货30','其他流动资产20','固定资产10','其他非流动资产2'],
//
//      color:['#1ab394','#2a84c4','#f6ab5e','#ec5064','#53dcd7'],//自定义颜色
//      calculable : true,
//      series : [
//          {
//              name:'年初',
//              type:'pie',
//              selectedMode: 'single',
//              radius: [0, '50%'],
//              center : ['50%', '28%'],//饼图位置
//              label: {
//                  normal: {
//                      position: 'inner',
//                      show:false//去掉饼图名字
//                  }
//              },
//              labelLine: {
//                  normal: {
//                      show: false
//                  }
//              },
//              data:[
//                  {value:56.3, name:'应收款项80'},
//                  {value:21.1, name:'存货30'},
//                  {value:14.1, name:'其他流动资产20'},
//                  {value:7.0, name:'固定资产10'},
//                  {value:1.5, name:'其他非流动资产2'},//selected:true默认分开
//              ]
//          }
//      ],
//      series_bak : [
//          {
//              name:'年初',
//              type:'pie',
//              selectedMode: 'single',
//              radius: [0, '50%'],
//              center : ['50%', '28%'],//饼图位置
//              label: {
//                  normal: {
//                      position: 'inner',
//                      show:false//去掉饼图名字
//                  }
//              },
//              labelLine: {
//                  normal: {
//                      show: false
//                  }
//              },
//              data:[
//                  {value:56.3, name:'应收款项80'},
//                  {value:21.1, name:'存货30'},
//                  {value:14.1, name:'其他流动资产20'},
//                  {value:7.0, name:'固定资产10'},
//                  {value:1.5, name:'其他非流动资产2'},//selected:true默认分开
//              ]
//          }
//      ]
//  };
//  // 自适应容器大小
//  myChart.setOption(option);
//  $(window).resize(function(){
//      myChart.resize();
//  })
//}






	function zcqk(){
		//资产情况echarts   
		// 基于准备好的dom，初始化echarts实例
		var myChart = echarts.init(document.getElementById("gdzcEcharts1"),{
		    noDataLoadingOption:{
				text: '暂无数据',
	            effect: 'bubble',
	            effectOption: {
	                effect: {
	                    n: 0
	                }
	            }
			}
		});
		option = {	
			title : {
		        text: '年初资产构成',
		        x: '10%',
		        y:'3%',
		        textStyle: {
			        color: '#000',// 图例文字颜色
			        fontSize:14,				        
			    }
	    	},
		    tooltip : {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },	
 			toolbox: {
                show : true,
                feature : {
                    myTool : {
                        show : true,
                        title : '修改',
                        icon : 'image://http://echarts.baidu.com/images/favicon.png',
                        onclick : function (){
                            modify_chart(myChart,'pie');
                        }
                    },
                    myTool2 : {
	                    show : true,
	                    title : '对比',
	                    icon : 'image://http://echarts.baidu.com/images/favicon.png',
	                    onclick : function (){
	                        show_init_chart_data(myChart);
	                    }
	                },
//	                saveAsImage : {show: true}
	            }
            },
		    legend: {
		        orient: 'horizontal',
		        x: '5%',
		        y:'80%',
		        itemWidth:10,
				itemHeight:10,
		        data:['应收款项','存货','其他流动资产','固定资产','其他非流动资产'],
		        itemGap:6,
		        textStyle: {
			        color: '#000',// 图例文字颜色
			        fontSize:13,				        
			    }
		    },
		    legend_bak: {
		        orient: 'horizontal',
		        x: '5%',
		        y:'80%',
		        itemWidth:10,
				itemHeight:10,
		        data:['应收款项','存货','其他流动资产','固定资产','其他非流动资产'],
		        itemGap:6,
		        textStyle: {
			        color: '#000',// 图例文字颜色
			        fontSize:13,				        
			    }
		    },
		    color:['#53dcd7','#2a84c4','#ec5064','#1ab394','#f6ab5e'],//自定义颜色
		    calculable : true,					  
		    series : [
		        {
		            name:'年初',
		            type:'pie',
		            selectedMode: 'single',
		            radius: [0, '50%'],
					center : ['45%', '45%'],//饼图位置
		            label: {
		                normal: {
//		                    position: 'inner',
		                    show:true,//去掉饼图名字
		                    formatter: '{c}%',
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: true
		                }
		            },
		            data:[
		                {value:9.05, name:'应收款项',selected:true},
		                {value:18.94, name:'存货',selected:true},
		                {value:0.35, name:'其他流动资产',selected:true},
		                {value:57.63, name:'固定资产'},
		                {value:14.03, name:'其他非流动资产',selected:true},//selected:true默认分开
		            ]
		        }
		    ],
		    series_bak : [
	            {
		            name:'年初',
		            type:'pie',
		            selectedMode: 'single',
		            radius: [0, '50%'],
					center : ['45%', '45%'],//饼图位置
		            label: {
		                normal: {
//		                    position: 'inner',
		                    show:true,//去掉饼图名字
		                    formatter: '{c}%',
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: true
		                }
		            },
		            data:[
		                {value:9.05, name:'应收款项'},
		                {value:18.94, name:'存货'},
		                {value:0.35, name:'其他流动资产'},
		                {value:57.63, name:'固定资产'},
		                {value:14.03, name:'其他非流动资产'},//selected:true默认分开
		            ]
		        }
	        ]
		};    								                    
	    // 自适应容器大小
	    myChart.setOption(option);
		$(window).resize(function(){
	    	myChart.resize();
	    })
	}		
	zcqk()	
	function zcqk1(){
		//资产情况echarts   
		// 基于准备好的dom，初始化echarts实例
		var myChart = echarts.init(document.getElementById("gdzcEcharts2"),{
		    noDataLoadingOption:{
				text: '暂无数据',
	            effect: 'bubble',
	            effectOption: {
	                effect: {
	                    n: 0
	                }
	            }
			}
		});
		option = {	
			title : {
		        text: '期末资产构成',
		        x: '10%',
		        y:'3%',
		        textStyle: {
			        color: '#000',// 图例文字颜色
			        fontSize:14,				        
			    }
	    	},
		    tooltip : {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },	
            toolbox: {
                show : true,
                feature : {
                    myTool : {
                        show : true,
                        title : '修改',
                        icon : 'image://http://echarts.baidu.com/images/favicon.png',
                        onclick : function (){
                            modify_chart(myChart,'pie');
                        }
                    },
                    myTool2 : {
	                    show : true,
	                    title : '对比',
	                    icon : 'image://http://echarts.baidu.com/images/favicon.png',
	                    onclick : function (){
	                        show_init_chart_data(myChart);
	                    }
	                },
//	                saveAsImage : {show: true}
                }
            },
		    legend: {
		        orient: 'horizontal',
		        x: '5%',
		        y:'80%',
		        itemWidth:10,
				itemHeight:10,
		        data:['应收款项','存货','其他流动资产','固定资产','其他非流动资产'],
		        itemGap:6,
		        textStyle: {
			        color: '#000',// 图例文字颜色
			        fontSize:13,				        
			    }
		    },
		    legend_bak: {
		        orient: 'horizontal',
		        x: '5%',
		        y:'80%',
		        itemWidth:10,
				itemHeight:10,
		        data:['应收款项','存货','其他流动资产','固定资产','其他非流动资产'],
		        itemGap:6,
		        textStyle: {
			        color: '#000',// 图例文字颜色
			        fontSize:13,				        
			    }
		    },
		    color:['#1ab394','#f6ab5e','#ec5064','#53dcd7','#2a84c4'],//自定义颜色
		    calculable : true,					  
		    series : [
		        {
		            name:'年末',
		            type:'pie',
		            selectedMode: 'single',
		            radius: [0, '50%'],
					center : ['45%', '45%'],//饼图位置
		            label: {
		                normal: {
//		                    position: 'inner',
		                    show:true,//去掉饼图名字
		                    formatter: '{c}%',
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: true
		                }
		            },
		            data:[
		                {value:6.49, name:'应收款项',selected:true},
		                {value:20.66, name:'存货',selected:true},
		                {value:0.05, name:'其他流动资产',selected:true},
		                {value:59.80, name:'固定资产'},
		                {value:13.00, name:'其他非流动资产',selected:true},//selected:true默认分开
		            ]
		        }
		    ],
		    series_bak : [
	            {
		            name:'年末',
		            type:'pie',
		            selectedMode: 'single',
		            radius: [0, '50%'],
					center : ['45%', '45%'],//饼图位置
		            label: {
		                normal: {
//		                    position: 'inner',
		                    show:true,//去掉饼图名字
		                    formatter: '{c}%',
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: true
		                }
		            },
		            data:[
		                {value:6.49, name:'应收款项'},
		                {value:20.66, name:'存货'},
		                {value:0.05, name:'其他流动资产'},
		                {value:59.80, name:'固定资产'},
		                {value:13.00, name:'其他非流动资产'},//selected:true默认分开
		            ]
		        }
	        ]
		};    								                    
	    // 自适应容器大小
	    myChart.setOption(option);
		$(window).resize(function(){
	    	myChart.resize();
	    })
	}		
	zcqk1()	

var tmp_chart;
var tmp_graphics;
//修改表格数据
function modify_chart(chart,graphics) {
    var target_chart = chart;
    tmp_chart = chart;
    tmp_graphics = graphics;
    //echarts.getInstanceByDom(document.getElementById("ydjnda2"));
    var opt = target_chart.getOption();
    var title = opt.title[0].text;
    //饼图
    if('pie' == graphics){
        var series = opt.series[0].data;
        console.log(series[0].name)
        //填充表格数据
        $("#data_table_list").empty();
        for (var i = 0; i < series.length; i++) {
            var html = " <tr> "+
//              "<td>" + i +"</td> "+
//              " <td>  <input type='text' class='xData' value='"+series[i].name+"'></td>  "+
				" <td>  <input type='text' style='text-align:right;border:none' readOnly='true' class='xData' value='"+series[i].name+"'></td>  "+
                " <td>  <input type='text' class='sData' value='"+series[i].value+"'></td>  "+
                "</tr>";
            $("#data_table_list").append(html);
        }
    }else{
        var xAxis = opt.xAxis[0].data;
        var series = opt.series[0].data;
        //填充表格数据
        $("#data_table_list").empty();
        for (var i = 0; i < xAxis.length; i++) {
            var html = " <tr> "+
//              "<td>" + i+"1" +"</td> "+
//              " <td>  <input type='text' class='xData' value='"+xAxis[i]+"'></td>  "+
				" <td>  <input type='text'  style='text-align:right;border:none' readOnly='true' class='xData' value='"+xAxis[i]+"'></td>  "+
                " <td>  <input type='text' class='sData' value='"+series[i]+"'></td>  "+
                "</tr>";
            $("#data_table_list").append(html);
        }
    }
    //弹出数据表格
    layer.open({
        title: ["<i class='fa fa-tag mgr5'></i>调整"+ title +"数据","background:#3492e9;font-size:14px;color:#fff"],
        skin: 'layui-layer-gray',
        fix: true,
        btn: ['关闭','更新图表'],
        btn1:function(){
        	layer.closeAll();
        },
        btn2:function(){
			update_data();
			layer.close();
        },
        area: ['400px', '392px'],
        type: 1,
        shadeClose: true,
        maxmin: false,
        content: $('#data_table_div'), //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
        success: function(){
			$(".layui-layer-btn").css({
				"background":"#f0f0f0",
				"text-align":"right",
			});
            $(".layui-layer-btn0").css({
            	"line-height":"26px",
                "border":"1px solid #3492e9",
                "color":"#3492e9",
                "background":"#f0f0f0",
            });
            $(".layui-layer-btn1").css({
            	"line-height":"26px",
                "border":"1px solid #3492e9",
                "color":"#fff",
                "background":"#3492e9",
            })
        }
    });
}

//更新图表
function update_data() {
    var xArr = new Array();
    $(".xData").each(function (data) {
        xArr.push($(this).val());
    })
    var sArr = new Array();
    $(".sData").each(function (data) {
        sArr.push($(this).val());
    })
    var target_chart = tmp_chart;
    //echarts.getInstanceByDom(document.getElementById("ydjnda2"));
    var opt = target_chart.getOption();
    //更新饼图
    if('pie' == tmp_graphics){
        var objArr = new Array();
        for (var i = 0; i<xArr.length ; i++){
            var obj = new Object();
            obj.name = xArr[i];
            obj.value = sArr[i];
            objArr.push(obj)
        }
        opt.series[0].data = objArr;
        opt.legend[0].data = objArr;
    }else{
        opt.xAxis[0].data = xArr;
        opt.series[0].data = sArr;
    }
    target_chart.setOption(opt);
    layer.closeAll();
    layer.msg('更新成功');
}

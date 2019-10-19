/**
 * Created by yiming on 2017/5/2.
 */

var tb_html;
function export2excle(aLink) {

    //添加<table>
    add_table_tag();
    //去掉隐藏的td
    remove_hidden_td(tb_html, 1);
    tb_html = encodeURIComponent(tb_html);
    aLink.href = "data:text/csv;charset=utf-8," + tb_html;
   // aLink.click();
}
//添加table标签
function add_table_tag() {
    //添加表头
    var head_html = $("#zycwzb1").parent().parent().prev().find('table').html();
    head_html = "<table border='1'>" + head_html;
    head_html = head_html + "</table>";

    tb_html = $("#zycwzb1").html();
    tb_html = "<table border='1'>" + tb_html;
    tb_html = tb_html + "</table>";

    tb_html = head_html + tb_html;

}
//去掉隐藏的td
function remove_hidden_td(html, index) {
    index = html.indexOf("display: none", index + 1);
    if (index != -1) {
        var pre_td_html = html.substring(0, index);
        //td 坐标开始
        var start_td_index = pre_td_html.lastIndexOf("<td")
        //td 坐标结束
        var end_td_index = html.indexOf("</td>", index);

        // console.log("========")
        // console.log("str index :" + index)
        // console.log("start : " + start_td_index)
        // console.log("end : " + end_td_index)
        //重新组合
        var start_html = html.substring(0, start_td_index);
        var end_html = html.substring(end_td_index);
        tb_html = start_html + end_html;

        remove_hidden_td(tb_html, index)
    }
}

package extractor.plugin

import myUtil.fileUtil._

object Output {
  
  def quote(int: Int) = "\"" + int.toString() + "\""
  
  def write = {
    println("Writing extracted type relations and call graph....")
    myUtil.fileUtil.writeOutputFile(PluginArgs.projectName, "nodes",
        "definition,id,name,kind\n" +
        Nodes.list.map { node =>
          List(node._2.source match {
                case Some(_) => "project"
                case None    => "external"
               },
               node._2.id, 
               node._2.name, 
               node._2.kind)
               .mkString(",")}.mkString("\n"))
         
    myUtil.fileUtil.writeOutputFile(PluginArgs.projectName, "edges", 
        "id1,edgeKind,id2\n" +
        Edges.list.map { edge =>
          List(edge.id1, edge.edgeKind, edge.id2).mkString(",")}.mkString("\n"))
    
    // see https://github.com/cpettitt/graphlib/wiki for Dagre graph specification
    myUtil.fileUtil.writeOutputFile(PluginArgs.projectName, "dagre", 
        Nodes.list.map(node => 
          "g.setNode(" + quote(node._2.id) + ", " +
                     "{ " + 
                       "name: " + "\"" + node._2.name + "\"" +
                       ", " +
                       "kind: " + "\"" + node._2.kind + "\"" +
                       ", " + "width:10" +
                       ", " + "height:10" +
                     " });")
          .mkString("\n") +
          
          "\n" +
           
        Edges.list.map(edge => 
          "g.setEdge(" + quote(edge.id1) + ", " + 
                      quote(edge.id2) + ", " +
                     "{ " +
                       "edgeKind: " + "\"" + edge.edgeKind + "\"" + 
                     " });")
          .mkString("\n"))
          
    Nodes.list.map(_._2).foreach(node =>
      if (node.source.isDefined)
        myUtil.fileUtil.writeOutputFile(PluginArgs.projectName, "node-source-" + node.id, 
                                        "< in file " + node.fileName.get + " >\n\n" + node.source.get.mkString("\n") + "\n"))
  }
  
}
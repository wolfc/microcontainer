/*
* JBoss, Home of Professional Open Source
* Copyright 2006, JBoss Inc., and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jboss.kernel.plugins.deployment.props.vertex;

import java.util.Set;

import org.jboss.kernel.plugins.deployment.props.TreeVertex;
import org.jboss.util.graph.Vertex;

/**
 * Simple holder vertex factory.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class HolderVertexFactory extends AbstractLevelVertexFactory
{
   public HolderVertexFactory()
   {
      super(Integer.MAX_VALUE);
   }

   public TreeVertex<?> createVertex(String name)
   {
      return new HolderVertex(name);
   }

   /**
    * Holder vertex.
    */
   static class HolderVertex extends TreeVertex<Vertex<String>>
   {
      public HolderVertex(String name)
      {
         super(name);
      }

      public void visit(Vertex<String> parent, Set<Vertex<String>> children)
      {
      }
   }
}

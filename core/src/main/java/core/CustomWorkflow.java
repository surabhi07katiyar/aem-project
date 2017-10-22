package core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.jcr.JcrConstants;

@Component
@Service
@Properties({
	@Property(name = Constants.SERVICE_DESCRIPTION, value = "Test Custom Workflow."),
	@Property(name = Constants.SERVICE_VENDOR, value = "Adobe"),
	@Property(name = "process.label" , value = "Create Custom Node")
})

public class CustomWorkflow implements WorkflowProcess {
	@Reference ResourceResolverFactory resourceResolverFactory;
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	public void execute(WorkItem workItem,WorkflowSession workflowSession, MetaDataMap metaDataMap){
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		try {
			Map<String,Object> param = new HashMap<String,Object>();
			/*Using Apache Sling User Mapper Service to get Resource Resolver */
			param.put(ResourceResolverFactory.SUBSERVICE, "writeService");
			ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);

			/* Getting Node for the Resource */
			Node root = resourceResolver.getResource("/content").adaptTo(Node.class);
			Session session = resourceResolver.adaptTo(Session.class);

			/* Creating a new node */
			Node newNode = root.addNode("workflowNode_" + timeStamp ,JcrConstants.NT_UNSTRUCTURED);

			/* Adding property to newly created node */
			newNode.setProperty(JcrConstants.JCR_DESCRIPTION,"New node from workflow");
			session.save();				

			/* Getting Workflow Payload */
			WorkflowData workflowData = workItem.getWorkflowData();
			String payloadPath = workflowData.getPayload().toString();
			System.out.println( "Payload Path for this workflow is: " + payloadPath );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("My AEM Project:Custom Workflow Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

}

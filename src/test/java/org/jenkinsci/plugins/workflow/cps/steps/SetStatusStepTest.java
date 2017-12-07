package org.jenkinsci.plugins.workflow.cps.steps;

import hudson.model.Result;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.flow.FlowExecution;
import org.jenkinsci.plugins.workflow.graph.BlockEndNode;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SetStatusStepTest {
    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void setStatus() throws Exception {
        WorkflowJob j = r.jenkins.createProject(WorkflowJob.class, "setStatus");
        j.setDefinition(new CpsFlowDefinition("stage('outermost') {\n" +
                "  stage('middle-unstable') {\n" +
                "    echo 'hi there again'\n" +
                "    status('SUCCESS')\n" +
                "    stage('inner-unstable') {\n" +
                "      status('UNSTABLE')\n" +
                "    }\n" +
                "  }\n" +
                "  stage('middle-set-failure') {\n" +
                "    echo 'hi there yet again'\n" +
                "    stage('inner-set-failure') {\n" +
                "      status('FAILURE')\n" +
                "    }\n" +
                "  }\n" +
                "  stage('middle-thrown-error') {\n" +
                "    echo('hi there')\n" +
                "    stage('middle-inner-success') {\n" +
                "      status('SUCCESS')\n" +
                "    }\n" +
                "    stage('middle-inner-nested-caught') {\n" +
                "      echo 'pre-catch'\n" +
                "      catchError {\n" +
                "        withEnv(['FOO=BAR']) {\n" +
                "          error('nested error')\n" +
                "        }\n" +
                "      }\n" +
                "      echo 'post-catch'\n" +
                "    }\n" +
                "    stage('middle-inner-error') {\n" +
                "      error('error')\n" +
                "    }\n" +
                "  }\n" +
                "}\n", true));
        WorkflowRun b = r.assertBuildStatus(Result.FAILURE, r.waitForCompletion(j.scheduleBuild2(0).waitForStart()));
        /*
Node dump follows, format:
[ID]{parent,ids}(millisSinceStartOfRun) flowNodeClassName stepDisplayName [st=startId if a block end node]
Action format:
	- actionClassName actionDisplayName
------------------------------------------------------------------------------------------
[2]{}FlowStartNode Start of Pipeline
[3]{2}StepStartNode Stage : Start
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[4]{3}StepStartNode outermost
  -BodyInvocationAction null
  -LabelAction outermost
[5]{4}StepStartNode Stage : Start
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[6]{5}StepStartNode middle-unstable
  -BodyInvocationAction null
  -LabelAction middle-unstable
[7]{6}StepAtomNode Print Message
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[8]{7}StepAtomNode Set stage or parallel branch status
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
  -FlowNodeStatusAction Status
[9]{8}StepStartNode Stage : Start
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[10]{9}StepStartNode inner-unstable
  -BodyInvocationAction null
  -LabelAction inner-unstable
[11]{10}StepAtomNode Set stage or parallel branch status
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
  -FlowNodeStatusAction Status
[12]{11}StepEndNode Stage : Body : End  [st=10]
  -BodyInvocationAction null
  -FlowNodeStatusAction Status
[13]{12}StepEndNode Stage : End  [st=9]
  -FlowNodeStatusAction Status
[14]{13}StepEndNode Stage : Body : End  [st=6]
  -BodyInvocationAction null
  -FlowNodeStatusAction Status
[15]{14}StepEndNode Stage : End  [st=5]
  -FlowNodeStatusAction Status
[16]{15}StepStartNode Stage : Start
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[17]{16}StepStartNode middle-set-failure
  -BodyInvocationAction null
  -LabelAction middle-set-failure
[18]{17}StepAtomNode Print Message
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[19]{18}StepStartNode Stage : Start
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[20]{19}StepStartNode inner-set-failure
  -BodyInvocationAction null
  -LabelAction inner-set-failure
[21]{20}StepAtomNode Set stage or parallel branch status
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
  -FlowNodeStatusAction Status
[22]{21}StepEndNode Stage : Body : End  [st=20]
  -BodyInvocationAction null
  -FlowNodeStatusAction Status
[23]{22}StepEndNode Stage : End  [st=19]
  -FlowNodeStatusAction Status
[24]{23}StepEndNode Stage : Body : End  [st=17]
  -BodyInvocationAction null
  -FlowNodeStatusAction Status
[25]{24}StepEndNode Stage : End  [st=16]
  -FlowNodeStatusAction Status
[26]{25}StepStartNode Stage : Start
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[27]{26}StepStartNode middle-thrown-error
  -BodyInvocationAction null
  -LabelAction middle-thrown-error
[28]{27}StepAtomNode Print Message
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[29]{28}StepStartNode Stage : Start
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[30]{29}StepStartNode middle-inner-success
  -BodyInvocationAction null
  -LabelAction middle-inner-success
[31]{30}StepAtomNode Set stage or parallel branch status
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
  -FlowNodeStatusAction Status
[32]{31}StepEndNode Stage : Body : End  [st=30]
  -BodyInvocationAction null
  -FlowNodeStatusAction Status
[33]{32}StepEndNode Stage : End  [st=29]
  -FlowNodeStatusAction Status
[34]{33}StepStartNode Stage : Start
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[35]{34}StepStartNode middle-inner-nested-caught
  -BodyInvocationAction null
  -LabelAction middle-inner-nested-caught
[36]{35}StepAtomNode Print Message
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[37]{36}StepStartNode Catch error and set build result : Start
  -LogActionImpl Console Output
[38]{37}StepStartNode Catch error and set build result : Body : Start
  -BodyInvocationAction null
[39]{38}StepStartNode Set environment variables : Start
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[40]{39}StepStartNode Set environment variables : Body : Start
  -BodyInvocationAction null
[41]{40}StepAtomNode Error signal
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
  -ErrorAction nested error
[42]{41}StepEndNode Set environment variables : Body : End  [st=40]
  -BodyInvocationAction null
  -ErrorAction nested error
[43]{42}StepEndNode Set environment variables : End  [st=39]
  -ErrorAction nested error
[44]{43}StepEndNode Catch error and set build result : Body : End  [st=38]
  -BodyInvocationAction null
  -ErrorAction nested error
  -LogActionImpl Console Output
[45]{44}StepEndNode Catch error and set build result : End  [st=37]
[46]{45}StepAtomNode Print Message
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[47]{46}StepEndNode Stage : Body : End  [st=35]
  -BodyInvocationAction null
[48]{47}StepEndNode Stage : End  [st=34]
[49]{48}StepStartNode Stage : Start
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
[50]{49}StepStartNode middle-inner-error
  -BodyInvocationAction null
  -LabelAction middle-inner-error
[51]{50}StepAtomNode Error signal
  -LogActionImpl Console Output
  -ArgumentsActionImpl null
  -ErrorAction error
[52]{51}StepEndNode Stage : Body : End  [st=50]
  -BodyInvocationAction null
  -ErrorAction error
[53]{52}StepEndNode Stage : End  [st=49]
  -ErrorAction error
[54]{53}StepEndNode Stage : Body : End  [st=27]
  -BodyInvocationAction null
  -ErrorAction error
[55]{54}StepEndNode Stage : End  [st=26]
  -ErrorAction error
[56]{55}StepEndNode Stage : Body : End  [st=4]
  -BodyInvocationAction null
  -ErrorAction error
[57]{56}StepEndNode Stage : End  [st=3]
  -ErrorAction error
  -ErrorAction error
[58]{57}FlowEndNode End of Pipeline  [st=2]
  -ErrorAction error
         */
        FlowExecution execution = b.getExecution();
        assertNotNull(execution);

        // The FlowEndNode should return FAILURE since it has an ErrorAction
        expectedNodeStatus(execution, "58", Result.FAILURE);

        // stage('outermost') is the same.
        expectedNodeStatus(execution, "57", Result.FAILURE);

        // stage('middle-inner-nested-caught') should return success for status, since its nested error was caught.
        expectedNodeStatus(execution, "47", Result.SUCCESS);

        // stage('middle-unstable') should return unstable.
        expectedNodeStatus(execution, "15", Result.UNSTABLE);

        // stage('inner-unstable') should return unstable.
        expectedNodeStatus(execution, "13", Result.UNSTABLE);

        // status('UNSTABLE') should return unstable
        expectedNodeStatus(execution, "11", Result.UNSTABLE);

        // status('SUCCESS') should return success
        expectedNodeStatus(execution, "8", Result.SUCCESS);

        // stage('middle-set-failure') should return failure.
        expectedNodeStatus(execution, "25", Result.FAILURE);

        // status('FAILURE') should return failure.
        expectedNodeStatus(execution, "21", Result.FAILURE);
    }

    private void expectedNodeStatus(@Nonnull FlowExecution execution, @Nonnull String nodeId, @Nonnull Result result) throws Exception{
        FlowNode n = execution.getNode(nodeId);
        assertNotNull(n);
        assertEquals(result, n.getStatus());
        if (n instanceof BlockEndNode) {
            assertEquals(result, ((BlockEndNode)n).getStartNode().getStatus());
        }
    }
}

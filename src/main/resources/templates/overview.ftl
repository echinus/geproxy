<li>
    <table>
    <#list fleetMovements as fleetMovement>
      <tr>
        <td><p>${fleetMovement.mission}</p></td>
        <td><p>${fleetMovement.from} &gt; ${fleetMovement.to}</p></td>
        <td><p>
        ${fleetMovement.startTime}<br/>${fleetMovement.eta}
          <#if fleetMovement.returnTime??><br/>${fleetMovement.returnTime}</#if>
        </p></td>
      </tr>
    </#list>
    </table>
</li>
</ul><#-- need this "unmatched" </ul> to close the list -->
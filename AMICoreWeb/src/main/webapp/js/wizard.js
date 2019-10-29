/*!
 * AMI Core
 *
 * Copyright (c) 2014-2019 The AMI Team / LPSC / IN2P3
 *
 * This file must be used under the terms of the CeCILL-C:
 * http://www.cecill.info/licences/Licence_CeCILL-C_V1-en.html
 * http://www.cecill.info/licences/Licence_CeCILL-C_V1-fr.html
 *
 */

/*--------------------------------------------------------------------------------------------------------------------*/

var charArray = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890';

/*--------------------------------------------------------------------------------------------------------------------*/

function generateEncryptionKey()
{
	var encryptionKey = [];

	for(var i = 0; i < 16; i++)
	{
		encryptionKey.push(charArray.charAt(
			Math.floor(charArray.length * Math.random())
		));
	}

	$('#encryption_key').val(encryptionKey.join(''));
}

/*--------------------------------------------------------------------------------------------------------------------*/

function validateForm()
{
	/*----------------------------------------------------------------------------------------------------------------*/

	var errors = [];

	var resetDB = false;

	$.each($(':input').serializeArray(), function(index, param) {

		if(param.name === 'router_reset')
		{
			resetDB = true;
		}

		if(param.name !== 'router_schema'
		   &&
		   param.name !== 'router_reset'
		   &&
		   param.name !== 'class_path'
		 ) {
			if(!param.value)
			{
				errors.push('Error, field `' + param.name + '` is empty!');
			}
		}
	});

	/*----------------------------------------------------------------------------------------------------------------*/

	if(errors.length > 0)
	{
		var message = '<div class="alert alert-danger alert-dismissible">\n'
		              +
		              '  <button class="close" data-dismiss="alert"><span>&times;</span><span class="sr-only">Close</span></button>\n'
		              +
		              '  <strong>Error!</strong> ' + errors.join(' ') + '\n'
		              +
		              '</div>'
		;

		$('#message').html(message);

		$(document).scrollTop(0);

		return false;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	if(resetDB && confirm('Reset the AMI database?') === false)
	{
		return false;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	$('button[type="submit"]').prop('disabled', true);

	/*----------------------------------------------------------------------------------------------------------------*/

	return true;
}

/*--------------------------------------------------------------------------------------------------------------------*/

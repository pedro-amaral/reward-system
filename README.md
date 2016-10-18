# Reward Sytem [![Build Status][travis-svg]][travis-link]
[>>Reward System on Heroku!][heroku-link]

This project is a code exercise that aims to solve a reward by invitation of users problem. It was developed based on [Luminus][luminus] template 
with [ring-swagger][swagger] and [MongoDB][mongodb] for data storage.

## Problem Description

A company is planning a way to reward customers for inviting their friends. They're planning a reward system that will
give a customer points for each confirmed invitation they played a part into. The definition of a confirmed invitation is one where another invitation's invitee invited someone.

The inviter gets (1/2)^k points for each confirmed invitation, where k is the level of the invitation: level 0 (people directly invited) yields 1 point, level 1 (people invited by someone invited by the original customer) gives 1/2 points, level 2 invitations (people invited by someone on level 1) awards 1/4 points and so on. Only the first invitation counts: multiple invites sent to the same person don't produce any further points, even if they come from different inviters.

Also, to count as a valid invitation, the invited customer must have invited someone (so customers that didn't invite anyone don't count as points for the customer that invited them).

## Prerequisites

You will need [Leiningen][lein] 2.0 or above installed. Plus, you need [MongoDB][mongodb].

## Development

To start the web server for the application, run:

    `$ lein run`

Run tests with:

    `$ lein test`

[swagger]: https://github.com/metosin/ring-swagger
[mongodb]: http://www.mongodb.com/
[luminus]: http://www.luminusweb.net/
[heroku-link]: https://reward-system-nubank.herokuapp.com/swagger-ui/index.html
[heroku-svg]: http://img.shields.io/badge/picturegallery-onHeroku-008eff.svg
[travis-link]: https://travis-ci.org/janraasch/picture-gallery
[travis-svg]: https://travis-ci.org/janraasch/picture-gallery.svg?branch=master
[lein]: https://github.com/technomancy/leiningen
